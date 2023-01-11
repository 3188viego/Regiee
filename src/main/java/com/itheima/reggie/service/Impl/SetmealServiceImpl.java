package com.itheima.reggie.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dto.SetmealDTO;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    SetmealMapper setmealMapper;

    @Autowired
    SetmealDishService setmealDishService;
    /**
     * 套餐保存同时保存setmeal_dish
     * @param setmealDTO
     */
    @Transactional//此方法涉及了多张表操作，所以要添加事务控制
    @Override
    public void saveWithDishes(SetmealDTO setmealDTO) {
        //先保存你套餐
        this.save(setmealDTO);
        //MyBatis-Plus会自动将id封装到setmealDTO中
        Long setmealId = setmealDTO.getId();
        //从setmealDTO中取出setmealDishes
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        //将setmealId设置到列表的每一个对象中
        setmealDishes=setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealId);
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }

    @Transactional
    @Override
    public SetmealDTO getSetmealWithDishById(Long setmeadId) {
        //现根据setmealId查询setmealDish
        //创建条件构造器
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId,setmeadId);
        //根据setmealId查询list
        List<SetmealDish> list = setmealDishService.list(setmealDishLambdaQueryWrapper);
        //再根据setmealID来查询setmeal
        Setmeal setmeal = this.getById(setmeadId);
        SetmealDTO setmealDTO = new SetmealDTO();
        BeanUtils.copyProperties(setmeal,setmealDTO);
        setmealDTO.setSetmealDishes(list);
        return setmealDTO;
    }
}
