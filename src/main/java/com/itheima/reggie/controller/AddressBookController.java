package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.AddressBook;
import com.itheima.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    AddressBookService addressBookService;

    /**
     * 设置默认路线
     * @param args
     * @return
     */
    @PutMapping("/default")
    public R<String> setDefault(@RequestBody Map<String,Object> args){
        //将原来默认路线addressBook的default设置为0
        LambdaUpdateWrapper<AddressBook> addressBookLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        addressBookLambdaUpdateWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId())
                .eq(AddressBook::getIsDefault,1).set(AddressBook::getIsDefault,0);
        addressBookService.update(addressBookLambdaUpdateWrapper);
        //将id为---的路线的default设置为1
        String id =(String) args.get("id");
        addressBookLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        addressBookLambdaUpdateWrapper.set(AddressBook::getIsDefault,1)
                .eq(AddressBook::getId,id);
        addressBookService.update(addressBookLambdaUpdateWrapper);
        return R.success("设置成功！");
    }

    /**
     * 获取默认地址
     * @return defaultAddressBook
     */
    @GetMapping("/default")
    public R<AddressBook> getDefault(){
        //获得当前用户的Id
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<AddressBook> addressBookLambdaQueryWrapper = new LambdaQueryWrapper<>();
        addressBookLambdaQueryWrapper.eq(AddressBook::getUserId,currentId).eq(AddressBook::getIsDefault,1);
        AddressBook one = addressBookService.getOne(addressBookLambdaQueryWrapper);
        return R.success(one);
    }

    /**
     * 添加地址
     * @param addressBook addressBook
     * @return String
     */
    @PostMapping
    public R<String> addAddress(@RequestBody AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookService.save(addressBook);
        return R.success("保存成功！");
    }

    /**
     * 获取地址列表
     * @return addressBooks
     */
    @GetMapping("/list")
    public R<List<AddressBook>> getAddressList(){
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<AddressBook> addressBookLambdaQueryWrapper = new LambdaQueryWrapper<>();
        addressBookLambdaQueryWrapper.eq(AddressBook::getUserId,currentId);
        List<AddressBook> list = addressBookService.list(addressBookLambdaQueryWrapper);
        return R.success(list);
    }

    /**
     * 更新addressBook时，先通过id获取id的值
     * @param id id
     * @return addressBook
     */
    @GetMapping("/{id}")
    public R<AddressBook> getAddressBookById(@PathVariable String id){
        LambdaQueryWrapper<AddressBook> addressBookLambdaQueryWrapper = new LambdaQueryWrapper<>();
        addressBookLambdaQueryWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId())
                .eq(AddressBook::getId,id);
        AddressBook one = addressBookService.getOne(addressBookLambdaQueryWrapper);
        return R.success(one);
    }

    /**
     * 跟新地址 addressBook
     * @return String
     */
    @PutMapping
    public R<String> updateAddressBook(@RequestBody AddressBook addressBook){
        addressBookService.updateById(addressBook);
        return R.success("更新成功");
    }

    /**
     * 根据id来删除地址
     * @param ids id
     * @return String
     */
    @DeleteMapping
    public R<String> deleteAddressBookById(String ids){
        addressBookService.removeById(ids);
        return R.success("删除成功！");
    }



}
