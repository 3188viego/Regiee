package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j//打印日志的注解
@RestController//前端控制器注解
@RequestMapping("/category")//控制器的映射路径注解
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @GetMapping("/page")
    public R<Page<Category>> page (int page,int pageSize){
        log.info("page={},pageSize={}",page,pageSize);
        Page<Category> pageInfo = new Page<>(page,pageSize);
        categoryService.page(pageInfo,null);
        return R.success(pageInfo);
    }

    /**
     * 添加菜品分类
     * @param category 菜品分类对象
     * @return 返回Stirng
     */
    @PostMapping
    public R<String> addCategory(@RequestBody Category category, HttpServletRequest request){
        Long uid =(Long) request.getSession().getAttribute("UID");
        //设置创建时间
        category.setCreateTime(LocalDateTime.now());
        //设置更新时间
        category.setUpdateTime(LocalDateTime.now());
        //设置修改user和创建user
        category.setCreateUser(uid);
        category.setUpdateUser(uid);
        boolean isSave = categoryService.save(category);
        if (isSave){
            return R.success("保存成功~");
        }else{
            return R.error("保存失败~");
        }
    }

    /**
     * 菜品分类的更新操作
     * @param category 菜品分类的对象
     * @param request HttpServletRequest
     * @return String
     */
    @PutMapping
    public R<String> update(@RequestBody Category category,HttpServletRequest request){
        Long uid =(Long) request.getSession().getAttribute("UID");
        category.setUpdateUser(uid);
        category.setUpdateTime(LocalDateTime.now());
        boolean result = categoryService.updateById(category);
        if (result){
            return R.success("修改成功~");
        }else{
            return R.error("修改失败");
        }
    }

    /**
     * 根据id删除方法
     * @param ids 客户端传入的id
     * @return String
     */
    @DeleteMapping
    public R<String> delete(Long ids){
        boolean result = categoryService.removeById(ids);
        if (result){
            return R.success("删除成功！");
        }else{
            return R.error("删除失败！");
        }
    }

//    /**
//     * 查询菜品分类列表
//     * @param type 类型
//     * @return 返回菜品分类
//     */
//    @GetMapping("list")
//    public R<List<Category>> list(int type){
//        log.info("已进入该方法");
//        List<Category> categoryByType = categoryService.getCategoryByType(type);
//        return R.success(categoryByType);
//    }
    /**
     * 查询菜品分类列表
     * @param type 类型
     * @return 返回菜品分类
     */
    @GetMapping("list")
    public R<List<Category>> list(String type){
        log.info("已进入该方法");
        if (type==null){
            List<Category> list = categoryService.list();
            return R.success(list);
        }
        List<Category> categoryByType = categoryService.getCategoryByType(Integer.parseInt(type));
        return R.success(categoryByType);
    }

}
