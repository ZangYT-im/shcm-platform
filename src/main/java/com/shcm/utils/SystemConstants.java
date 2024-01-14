package com.shcm.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;




@Component
public class SystemConstants {
//    public static final String IMAGE_UPLOAD_DIR = "D:\\workspace\\IDEA\\nginx-1.18.0\\html\\shcm\\imgs";

    //图片上传地址,这里选择采用动态读取配置文件,因为本地的地址和服务器地址不一样
    public static String IMAGE_UPLOAD_DIR;

    public static final String USER_NICK_NAME_PREFIX = "user_";
    public static final int DEFAULT_PAGE_SIZE = 5;

    //todo 這裏應該做成滾動刷新
    //主頁博客查詢時的最大數量
    public static final int MAX_PAGE_SIZE = 999;


    @Value("${IMAGE_UPLOAD_DIR}")
    public void SystemConstants(String IMAGE_UPLOAD_DIR) {
        this.IMAGE_UPLOAD_DIR = IMAGE_UPLOAD_DIR;
    }
}
