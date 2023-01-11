package com.itheima.reggie.dto;

import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO:data transfer object    数据传输对象
 */
@Data
public class DishDTO extends Dish {

    private List<DishFlavor> flavors=new ArrayList<>();

    //下面这两个属性现在还使用不到
    private String categoryName;

    private Integer copies;

}
