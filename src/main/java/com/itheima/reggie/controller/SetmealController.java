package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDTO;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealDishMapper;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.itheima.reggie.controller.DishController.getList;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    SetmealService setmealService;

    @Autowired
    SetmealDishMapper setmealDishMapper;

    @Autowired
    SetmealDishService setmealDishService;

    @Autowired
    DishService dishService;

    /**
     * 分页查询
     * @param page 当前页码
     * @param pageSize 每一个页面所包含的数据条数
     * @param name 按套餐名字查询
     * @return page<Setmeal>
     */
    @GetMapping("/page")
    public R<Page<Setmeal>> page(int page, int pageSize, String name){
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        QueryWrapper<Setmeal> setmealQueryWrapper = new QueryWrapper<>();
        setmealQueryWrapper.like(StringUtils.isNotBlank(name),"name",name);
        setmealService.page(pageInfo,setmealQueryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 批量删除方法
     * @param ids ids
     * @return String
     */
    @DeleteMapping
    public R<String> deleteBath(String ids){
        ArrayList<String> idList = getList(ids);
        setmealService.removeByIds(idList);
        return R.success("删除成功~");
    }

    /**
     * 套餐保存
     * @param setmealDTO 数据传输对象
     * @return String
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDTO setmealDTO){
        log.info("以及如套餐添加Servlet");
        log.info("setmealDTO={}",setmealDTO);
        setmealService.saveWithDishes(setmealDTO);
        return R.success("添加成功");
    }

    /**
     * getSetmealWithDish
     * @return R
     */
    @GetMapping("/{id}")
    public R<SetmealDTO> getMessageByID(@PathVariable Long id){
        SetmealDTO setmealDTO = setmealService.getSetmealWithDishById(id);
        return R.success(setmealDTO);
    }

    /**
     * 修改套餐
     * @param setmealDTO 数据传输对象
     * @return String
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDTO setmealDTO, HttpServletRequest request){
        //设置setmeal_id
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes=setmealDishes.stream().map(item->{
            item.setSetmealId(setmealDTO.getId());
            item.setUpdateTime(LocalDateTime.now());
            item.setUpdateUser((Long) request.getSession().getAttribute("UID"));
            return item;
        }).collect(Collectors.toList());

        //先将传入来的列表中没有的菜品删除掉
        setmealDishMapper.deleteNotInIds(setmealDTO);
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getId,setmealDTO.getId());
        //更新setmeal
        setmealService.update(setmealDTO,setmealLambdaQueryWrapper);
        //更新setmeal_dish setmeal_sort默认值为0
        setmealDishService.replace(setmealDishes);
        return R.success("更新成功！");
    }


    /**
     * 修改状态
     * @param ids
     * @param status
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(String ids,@PathVariable int status){
        ArrayList<String> idList = getList(ids);
        setmealService.updateStatusById(idList,status);
        return R.success("状态修改成功！");
    }

    /**
     * 根据ID获取套餐中的菜品信息
     * @param categoryId 套餐id
     * @param status 状态
     * @return Dishes
     */
    @GetMapping("/list")
    public R<List<SetmealDish>> getlist(String categoryId, int status){
        LambdaQueryWrapper<Setmeal> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Setmeal::getCategoryId,categoryId);
        Setmeal one = setmealService.getOne(dishLambdaQueryWrapper);
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId,one.getId());
        List<SetmealDish> setmealDishes = setmealDishService.list(setmealDishLambdaQueryWrapper);
        return R.success(setmealDishes);
    }
}
