package com.itheima.reggie.common;

/**
 * 基于ThreadLocal封装的工具类，用于保存或获取当前登录用户的id
 */
public class BaseContext {

    private static ThreadLocal<Long> threadLocal=new InheritableThreadLocal<>();

    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
