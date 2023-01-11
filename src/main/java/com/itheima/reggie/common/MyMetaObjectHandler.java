package com.itheima.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


/**
 * MetaObjectHandle
 *   源数据处理器
 */
@Component//Spring组件注解
@Slf4j//日志注解
public class MyMetaObjectHandler implements MetaObjectHandler {
    /**
     * 插入自动填充
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        Long currentId = BaseContext.getCurrentId();
        log.info("公共字段自动填充【insert】");
        log.info(metaObject.toString());
        log.info("已从BaseContext中获取到当前的UID,UID={}",currentId);
        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("updateUser",currentId);
        metaObject.setValue("createTime",LocalDateTime.now());
        metaObject.setValue("createUser",currentId);
    }

    /**
     * 更新自动填充
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        Long currentId = BaseContext.getCurrentId();
        log.info("公共字段自动填充【update】");
        log.info(metaObject.toString());
        log.info("已从BaseContext中获取到当前的UID,UID={}",currentId);
        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("updateUser",currentId);
    }
}
