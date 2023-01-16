package com.itheima.reggie.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.entity.*;
import com.itheima.reggie.mapper.OrderMapper;
import com.itheima.reggie.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Repository
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

//    @Autowired
//    OrderMapper orderMapper;

    @Autowired
    ShoppingCartService shoppingCartService;

    @Autowired
    UserService userService;

    @Autowired
    OrderDetailService orderDetailService;

    @Autowired
    AddressBookService addressBookService;



    @Transactional//这里要操作多张表，因此需要开启事务控制
    @Override
    public void submit(Orders orders) {
        //1.获取当前用户的id
        Long currentId = BaseContext.getCurrentId();
        //2.查询购物车中的数据
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,currentId);
        List<ShoppingCart> list = shoppingCartService.list(shoppingCartLambdaQueryWrapper);//获取购物车信息
        if (list==null||list.size()==0){
            throw new RuntimeException("购物车为空不能下单");
        }
        User user = userService.getById(currentId);//获取user的信息
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());//获取地址信息
        if (addressBook==null){
            throw  new RuntimeException("地址为空，不能下单~");
        }
        BigDecimal totalAmount = getTotalAmount(list); //购物车中的总金额
        long id = IdWorker.getId();//生成订单号
        //3.添加order表
        //封装orders
//        orders.setId();
        orders.setNumber(String.valueOf(id));//设置订单号
        orders.setAddress(addressBook.getDetail());//地址
        orders.setConsignee(addressBook.getConsignee());//收货人
        orders.setAmount(totalAmount);//设置总金额
        orders.setUserId(currentId);//设置当前用户的Id
        orders.setStatus(2);//设置付款状态 1为待付款
        orders.setOrderTime(LocalDateTime.now());//下单时间
        orders.setPhone(user.getPhone());//电话
        orders.setUserName(user.getName());//user_name
        orders.setCheckoutTime(LocalDateTime.now());//设置结账时间
        this.save(orders);//保存orders
        //4.添加orderDetail表

        ArrayList<OrderDetail> orderDetails = new ArrayList<>();
        for (ShoppingCart shoppingCart : list) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setDishFlavor(shoppingCart.getDishFlavor());
            orderDetail.setName(shoppingCart.getName());
            orderDetail.setImage(shoppingCart.getImage());
            orderDetail.setOrderId(orders.getId());
            orderDetail.setDishId(shoppingCart.getDishId());
            orderDetail.setSetmealId(shoppingCart.getSetmealId());
            orderDetail.setNumber(shoppingCart.getNumber());
            orderDetail.setAmount(shoppingCart.getAmount());
            orderDetails.add(orderDetail);
        }
        orderDetailService.saveBatch(orderDetails);
        //清空购物车
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper1 = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper1.eq(ShoppingCart::getUserId,currentId);
        shoppingCartService.remove(shoppingCartLambdaQueryWrapper1);
    }

    public static BigDecimal getTotalAmount(List<ShoppingCart> list){
        Long totalAmount=0l;
        for (ShoppingCart item : list) {
            totalAmount+= item.getAmount().longValue() *item.getNumber();
        }
        BigDecimal bigDecimal = BigDecimal.valueOf(totalAmount);
        return bigDecimal;
    }
}
