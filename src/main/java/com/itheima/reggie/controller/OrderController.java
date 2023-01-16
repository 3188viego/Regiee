package com.itheima.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    /**
     * 用户下单
     * @param orders 订单
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        orderService.submit(orders);
        return R.success("成功");
    }

    /**
     * 对订单进行分页查询
     * @param page 当前页码
     * @param pageSize 每一页的所包含的数据个数
     * @return page
     */
    @GetMapping("/page")
    public R<Page<Orders>> getOrders(int page, int pageSize, String number, String beginTime,String endTime){
        log.info("已进入getOrders方法");
        log.info("page={},pageSize={},number={},beginTime={},endTime={}",page,pageSize,number,beginTime,endTime);
        Page<Orders> ordersPage = new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ordersLambdaQueryWrapper.between(Orders::getOrderTime,beginTime,endTime);
        orderService.page(ordersPage);
        return R.success(ordersPage);
    }

    /**
     * 用户查询账单 分页查询
     * @param page 当前页码
     * @param pageSize 每一页数据条数
     * @return page
     */
    @GetMapping("/userPage")
    public R<Page<Orders>> getUserOrder(int page,int pageSize){
        Page<Orders> ordersPage = new Page<>();
        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ordersLambdaQueryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId());
        orderService.page(ordersPage,ordersLambdaQueryWrapper);
        return R.success(ordersPage);
    }

    /**
     * 更改order的status
     * @param args id
     * @return string
     */
    @PutMapping
    public R<String> changeStatus(@RequestBody Map<String,Object> args){
        String id = (String)args.get("id");
        Integer status=(Integer) args.get("status");
        LambdaUpdateWrapper<Orders> ordersLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        ordersLambdaUpdateWrapper.set(Orders::getStatus,status)
                .eq(Orders::getId,id);
        orderService.update(ordersLambdaUpdateWrapper);
        return R.success("账单状态修改成功");
    }
}
