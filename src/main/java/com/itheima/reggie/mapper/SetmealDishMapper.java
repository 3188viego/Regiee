package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.dto.SetmealDTO;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface SetmealDishMapper extends BaseMapper<SetmealDish> {


    //删除掉新的dto中没有的
    void deleteNotInIds(@Param("setmealDTO")SetmealDTO setmealDTO);

//    //更新或插入新的数据
//    void replace(@Param("setmealDish")SetmealDish  setmealDish);
}
