package com.itheima.reggie.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDTO;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.JsonbHttpMessageConverter;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j//打印日志的注解
@RestController//前端控制器注解
@RequestMapping("/dish")//控制器的映射路径注解
public class DishController {

    @Autowired
    DishService dishService;

    /**
     * 菜品分页查询
     * @param page 当前页码
     * @param pageSize 每一页的数据量
     * @param name 按名字查询
     * @return page对象
     */
    @GetMapping("/page")
    public R<Page<Dish>> page(int page,int pageSize,String name){
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        QueryWrapper<Dish> dishQueryWrapper = new QueryWrapper<>();
        dishQueryWrapper.like(StringUtils.isNotBlank(name),"name",name);
        dishService.page(pageInfo,dishQueryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 菜品保存
     * @param dishDTO 菜品数据传输对象
     * @return 返回String
     */
    @PostMapping
    public R<String> save(@RequestBody DishDTO dishDTO){
        log.info("已进入此方法~");
        log.info("dishDTO={}",dishDTO.toString());
        dishService.saveWithFlavor(dishDTO);
        return R.success("保存成功");
    }

    /**
     * 批量删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteBath(String ids){
        log.info("ids={}",ids);
        ArrayList<String> idList = new ArrayList<>();
        String[] split = ids.split(",");
        for (String s : split) {
            idList.add(s);
        }
        dishService.deleteByIds(idList);
        return R.success("删除成功！");
    }

    /**
     * 修改菜品信息前，先根据id查询到菜品和菜品口味的信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDTO> get(@PathVariable Long id){
        log.info("id={}",id);
        DishDTO byIdWithFlavor = dishService.getByIdWithFlavor(id);
        return R.success(byIdWithFlavor);
    }

    /**
     * 菜品的修改方法
     * @param dishDTO
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDTO dishDTO){
        dishService.updateWithFlavor(dishDTO);
        return R.success("操作成功");
    }

    /**
     * 批量修改菜品的状态
     * @param status 状态
     * @param ids ids
     * @return String
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable int status,String ids){
        LambdaUpdateWrapper<Dish> dishLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        ArrayList<String> idList = getList(ids);
        dishService.updateStatusByIds(status,idList);
        return R.success("修改成功~");
    }

//    /**
//     * 第一个方案:当时还没有开发front
//     * 根据CategoryId来查询菜品列表
//     * @param categoryId
//     * @return
//     */
//    @GetMapping("/list")
//    public R<List<Dish>> getDishList(Long categoryId){
//        List<Dish> dishes = dishService.getByCategoryId(categoryId);
//        return R.success(dishes);
//    }

//    /**
//     * 第二个方案：已经开发了front,但是，getDishFlavorByDish方法访问数据库的频率过高，为了优化，减少访问数据库的次数
//     * 根据CategoryId来查询菜品列表
//     * @param categoryId
//     * @return
//     */
//    @GetMapping("/list")
//    public R<List<DishDTO>> getDishList(Long categoryId){
//        List<Dish> dishList = dishService.getByCategoryId(categoryId);
//        List<DishDTO> dishDTOS = new ArrayList<>();
//        for (Dish dish : dishList) {
//            DishDTO dishDTO = new DishDTO();
//            BeanUtils.copyProperties(dish,dishDTO);
//            dishDTOS.add(dishDTO);
//        }
//        dishDTOS=dishDTOS.stream().map(item->{
//            Long dishID = item.getId();
//            List<DishFlavor> dishFlavors=dishService.getDishFlavorByDishID(dishID);
//            item.setFlavors(dishFlavors);
//            return item;
//        }).collect(Collectors.toList());
//        return R.success(dishDTOS);
//    }

    /**
     * 第二个方案：已经开发了front,但是，getDishFlavorByDish方法访问数据库的频率过高，为了优化，减少访问数据库的次数
     * 可以用Redis非关系型数据库来进行缓存优化
     * 根据CategoryId来查询菜品列表
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDTO>> getDishList(Long categoryId){
        List<Dish> dishList = dishService.getByCategoryId(categoryId);
        List<DishDTO> dishDTOS = new ArrayList<>();
        for (Dish dish : dishList) {
            DishDTO dishDTO = new DishDTO();
            BeanUtils.copyProperties(dish,dishDTO);
            dishDTOS.add(dishDTO);
        }
        dishDTOS=dishDTOS.stream().map(item->{
            Long dishID = item.getId();
            List<DishFlavor> dishFlavors=dishService.getDishFlavorByDishID(dishID);
            item.setFlavors(dishFlavors);
            return item;
        }).collect(Collectors.toList());
        return R.success(dishDTOS);
    }

    public static ArrayList<String> getList(String ids){
        String[] split = ids.split(",");
        ArrayList<String> arrayList = new ArrayList<>();
        for (String s : split) {
            arrayList.add(s);
        }
        return arrayList;
    }
}
