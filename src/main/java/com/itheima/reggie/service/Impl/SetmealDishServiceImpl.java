package com.itheima.reggie.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealDishMapper;
import com.itheima.reggie.service.SetmealDishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {

    @Autowired
    SetmealDishMapper setmealDishMapper;

    @Transactional
    @Override
    public void replace(List<SetmealDish> setmealDishes) {
       setmealDishes.forEach(item->{
           if (item.getCreateUser()==null){
               //新增
               this.save(item);
           }else{
               //更新
               this.saveOrUpdate(item);
           }
       });
    }
}
