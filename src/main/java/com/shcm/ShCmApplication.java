package com.shcm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.mybatis.spring.annotation.MapperScan;

/**
 * <p>
 * description
 * </p>
 *
 * @author ZangYT
 * @since 2023/11/12 13:02
 */

@MapperScan("com.shcm.mapper")
@SpringBootApplication
public class ShCmApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShCmApplication.class,args);
        System.out.println("项目启动成功~");
    }
}
