package com.stylefeng.guns.rest.common;

import com.stylefeng.guns.api.UserInfoModel;

public class CurrentUser {

    //线程绑定的存储空间
    private static final ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public static void saveUserInfo(String userId){

        threadLocal.set(userId);
    }

    public static String getCurrentUser(){

        return threadLocal.get();
    }

//    这种方式由于每个线程都会创建，如果threadlocal存储数据太多，会造成jvm负载太重
//    将用户信息放入存储空间
//    public static void saveUserInfo(UserInfoModel userInfoModel){
//
//        threadLocal.set(userInfoModel);
//    }
//
//    将用户信息取出
//    public static UserInfoModel getCurrentUser(){
//
//        return threadLocal.get();
//    }

}
