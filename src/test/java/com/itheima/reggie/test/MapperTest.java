package com.itheima.reggie.test;

import com.itheima.reggie.dto.SetmealDTO;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealDishMapper;
import com.itheima.reggie.service.SetmealDishService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class MapperTest {
    @Autowired
    SetmealDishMapper setmealDishMapper;

    @Autowired
    SetmealDishService setmealDishService;

}
