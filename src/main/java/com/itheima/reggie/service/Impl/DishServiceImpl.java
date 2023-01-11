package com.itheima.reggie.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dto.DishDTO;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishFlavorMapper;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    DishFlavorService dishFlavorService;
    /**
     * 保存菜品的同时，保存口味
     * @param dishDTO
     */
    @Transactional//因为本方法用到了多表操作，所以要添加Transactional事务控制，要使此注解生效的话~要在启动类上开启此注解
    @Override
    public void saveWithFlavor(DishDTO dishDTO) {
        //先保存菜品
        this.save(dishDTO);
        //菜品保存完成之后，会自动封装id
        Long id = dishDTO.getId();
        //菜品口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        //将列表中的每一个对象都拿出来，setDishId之后在返回
        flavors=flavors.stream().map((item)->{
            item.setDishId(id);
            return item;
        }).collect(Collectors.toList());
        //保存菜品口味
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据ids批量删除菜品{先删除dish_flavor然后再删除dish}
     * @param ids
     */
    @Transactional
    @Override
    public void deleteByIds(ArrayList<String> ids){
        //先删除dish_flavor
        dishFlavorService.removeByIds(ids);
        //然后再删除菜品
        this.removeByIds(ids);
    }

    /**
     * 根据id查询菜品信息和口味信息
     * @param id 菜品id
     * @return DTO
     */
    @Transactional
    @Override
    public DishDTO getByIdWithFlavor(Long id){
        //先查dish查询菜品基本信息
        Dish dish = this.getById(id);
        //创建DTO对象
        DishDTO dishDTO = new DishDTO();
        BeanUtils.copyProperties(dish,dishDTO);
        //在查dish_flavor菜品对应的口味信息
        LambdaUpdateWrapper<DishFlavor> dishFlavorLambdaUpdateWrapper = new LambdaUpdateWrapper<DishFlavor>();
        dishFlavorLambdaUpdateWrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> flavors = dishFlavorService.list(dishFlavorLambdaUpdateWrapper);
        //将flavors封装到disDTO中
        dishDTO.setFlavors(flavors);
        return dishDTO;
    }

    /**
     * 菜品更新同时更新口味
     * @param dishDTO
     */
    @Transactional
    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        //先更新dish表中的数据
        this.updateById(dishDTO);
        //再根据dishID来删除dish_flavor中的数据
        LambdaUpdateWrapper<DishFlavor> dishFlavorLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        dishFlavorLambdaUpdateWrapper.eq(DishFlavor::getDishId,dishDTO.getId());
        dishFlavorService.remove(dishFlavorLambdaUpdateWrapper);
        //在向dish_flavor插入新的flavor数据
        dishFlavorService.saveBatch(dishDTO.getFlavors());
    }

    /**
     * 批量更新Status
     * @param status
     * @param idList
     */
    @Override
    public void updateStatusByIds(int status, ArrayList<String> idList) {
        LambdaUpdateWrapper<Dish> dishLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        dishLambdaUpdateWrapper.set(Dish::getStatus,status);
        for (String s : idList) {
            dishLambdaUpdateWrapper.eq(Dish::getId,s).or();
        }
        this.update(dishLambdaUpdateWrapper);
    }

    /**
     * 根据categoryId来查询dish
     * @param categoryId
     */
    @Override
    public List<Dish> getByCategoryId(Long categoryId) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,categoryId);
        List<Dish> dishes = this.list(dishLambdaQueryWrapper);
        return dishes;
    }
}
