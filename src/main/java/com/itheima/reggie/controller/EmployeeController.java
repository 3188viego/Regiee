package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.common.R;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;//注入service依赖

    /**
     * 员工登录方法
     */
//    @ResponseBody
    @PostMapping("/login")
    public R<Employee> login(@RequestBody Employee employee, HttpServletRequest request){
        /**
         * 过程分析：
         *      1.将页面提交的password进行md5加密处理
         *      2.根据页面提交的用户名username查询数据库
         *      3.如果没有查询到，则返回登录失败的结果
         *      4.密码比对，如果不一致，则返回登录失败的结果
         *      5.查询员工的状态，如果已禁用状态，则返回员工以禁用的结果
         *      6.登录成功，将员工的ID存入session中并返回登录成功的结果
         */
        //1.将页面提交的password进行md5加密处理
        String password = employee.getPassword();
        password=DigestUtils.md5DigestAsHex(password.getBytes());
        // 2.根据页面提交的用户名username查询数据库
        //创建lambdaQueryWrapper对象
        LambdaQueryWrapper<Employee> employeeLambdaQueryWrapper = new LambdaQueryWrapper<Employee>();
        //封装查询条件
        employeeLambdaQueryWrapper.eq(Employee::getUsername,employee.getUsername());
        //调用service.getOne方法，得到one对象
        Employee one = employeeService.getOne(employeeLambdaQueryWrapper);
        //3.如果没有查询到，则返回登录失败的结果
        if (one==null){
            //用户名查询失败
            return R.error("用户名不存在失败");
        }else{
            //用户名查询成功
            //进行密码比对
            if (password.equals(one.getPassword())){
                //密码比对成功
                if (one.getStatus()==1){
                    //员工状态未被禁用，登录成功，将one存入session中
                    request.getSession().setAttribute("UID",one.getId());
                    return R.success(one);
                }else{
                    //员工状态被禁用
                    return R.error("员工状态被禁用");
                }
            }else{
                //密码比对失败
                return R.error("密码错误");
            }
        }
    }

    /**
     * 员工退出登录方法
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("UID");
        return R.success("退出成功");
    }

    /**
     * 新增员工方法
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Employee employee,HttpServletRequest request){
        //统一初始密码
        String password = DigestUtils.md5DigestAsHex("123456".getBytes());
        employee.setPassword(password);
        //给employee设置createTime,updateTime,createUser,updateUser
        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());
        //employee.setCreateUser((Long) request.getSession().getAttribute("UID"));
        //employee.setUpdateUser((Long) request.getSession().getAttribute("UID"));
        //保存
        //1.局部异常处理
        /**
       try{

           employeeService.save(employee);
       }catch (Exception e ){
           return R.error("添加失败");
       }
        return R.success("添加成功");
         **/
        //2.全局异常处理，创建GlobalExceptionHandler
        employeeService.save(employee);
        return R.success("添加成功");
    }


    /**
     * 员工信息的分页查询
     * @param page 页码
     * @param pageSize 页面的大小
     * @param name 按姓名查询
     * Page是mybatisPlus封装的一个类，用来进行分页查询
     * @return R<Page>
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page={},pageSize={},name={}",page,pageSize,name);
        //创建分页构造器
        Page pageInfo=new Page(page,pageSize);
        //创建条件构造器
        LambdaQueryWrapper<Employee> employeeLambdaQueryWrapper=new LambdaQueryWrapper<>();
        //like()方法:
        //第一个参数：StringUtils.isNotEmpty(name)条件满足的情况下，就会执行Like查询
        //第二个参数：Employee::getName是Lambda表达式，函数接口
        //第三个参数：name是要查询的名字
        employeeLambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //创建排序条件
        employeeLambdaQueryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo, employeeLambdaQueryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 根据id修改员工信息
     * @param employee 员工对象
     * @param request request对象
     * @return 返回String
     */
    @PutMapping
    public R<String> update (@RequestBody Employee employee,HttpServletRequest request){
        Long uid = (Long)request.getSession().getAttribute("UID");
        log.info(employee.toString());
        //如果实体对象中没有username则证明是禁止启用操作
//        UpdateWrapper<Employee> employeeUpdateWrapper = new UpdateWrapper<>();
//        employeeUpdateWrapper.set("status",employee.getStatus())
//                .set("update_time", LocalDateTime.now())
//                .set("update_user",uid)
//                .set(StringUtils.isNotBlank(employee.getUsername()),"username",employee.getUsername())
//                .set(StringUtils.isNotBlank(employee.getName()),"name",employee.getName())
//                .set(StringUtils.isNotBlank(employee.getSex()),"sex",employee.getSex())
//                .set(StringUtils.isNotBlank(employee.getPhone()),"phone",employee.getPhone())
//                .set(StringUtils.isNotBlank(employee.getIdNumber()),"id_number",employee.getIdNumber())
//                .eq("id",employee.getId());
        //boolean update = employeeService.update(employeeUpdateWrapper);
        boolean b = employeeService.updateById(employee);
        if (b){
            return R.success("修改成功~");
        }else{
            return R.success("修改失败~");
        }
    }

    /**
     * 根据id查询员工信息，用户edit
     * @param id 员工id
     * @return 返回员工对象
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        QueryWrapper<Employee> employeeQueryWrapper = new QueryWrapper<>();
        employeeQueryWrapper.eq("id",id);
        Employee one = employeeService.getOne(employeeQueryWrapper);
        return R.success(one);
    }
}

