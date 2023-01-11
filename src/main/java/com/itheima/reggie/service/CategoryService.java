package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {

    public List<Category> getCategoryByType(int type);
}
