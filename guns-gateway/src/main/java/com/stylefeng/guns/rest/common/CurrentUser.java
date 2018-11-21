package com.stylefeng.guns.rest.common;

public class CurrentUser {

    //线程绑定的存储空间
    //InheritableThreadLocal相比ThreadLocal来说在线程切换后会将之前的线程绑定信息保存下来
    private static final InheritableThreadLocal<String> threadLocal = new InheritableThreadLocal<>();

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
