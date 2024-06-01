//package com.shcm.utils;
//
//
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//@Component
//public class ConstantPropertiesUtils implements InitializingBean {
//
//    //读取配置文件
//    @Value("${cloud.tencent.oss.endpoint}")
//    private String endpoint;
//
//    @Value("${cloud.tencent.oss.secretId}")
//    private String keyId;
//
//    @Value("${cloud.tencent.oss.secretKey}")
//    private String keySecret;
//
//    @Value("${cloud.tencent.oss.bucketName}")
//    private String bucketName;
//
//
//
//    //定义公开静态常量
//    public static String END_POINT;
//    public static String ACCESS_KEY_ID;
//    public static String ACCESS_KEY_SECRET;
//    public static String BUCKET_NAME;
//    public static String FILE_HOST ;
//
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        END_POINT = endpoint;
//        ACCESS_KEY_ID = keyId;
//        ACCESS_KEY_SECRET = keySecret;
//        BUCKET_NAME = bucketName;
////        FILE_HOST = fileHost;
//
//    }
//}
