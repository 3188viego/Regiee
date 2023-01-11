package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDTO;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

import static com.itheima.reggie.controller.DishController.getList;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    SetmealService setmealService;

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
     * @param setmealDTO
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDTO setmealDTO){
        log.info("以及如套餐添加Servlet");
        log.info("setmealDTO={}",setmealDTO);
        setmealService.saveWithDishes(setmealDTO);
        return R.success("添加成功");
    }

    /**
     * getSetmealWithDihs
     * @return R
     */
    @GetMapping("/{id}")
    public R<SetmealDTO> getMessageByID(@PathVariable Long id){
        SetmealDTO setmealDTO = setmealService.getSetmealWithDishById(id);
        return R.success(setmealDTO);
    }
}
