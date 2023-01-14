package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDTO;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;

import java.util.ArrayList;
import java.util.List;

public interface DishService extends IService<Dish> {

    public void saveWithFlavor(DishDTO dishDTO);

    public void deleteByIds(ArrayList<String> ids);

    public DishDTO getByIdWithFlavor(Long id);

    void updateWithFlavor(DishDTO dishDTO);

    void updateStatusByIds(int status, ArrayList<String> idList);

    List<Dish> getByCategoryId(Long categoryId);

    List<DishFlavor> getDishFlavorByDishID(Long dishID);
}
