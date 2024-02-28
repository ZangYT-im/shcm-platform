package com.shcm;

import com.shcm.utils.EnvConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

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
@EnableAspectJAutoProxy(exposeProxy = true)//暴露动态代理的对象
public class ShCmApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShCmApplication.class,args);
        System.out.println("项目启动成功~");
        System.out.println("当前环境：" +  EnvConfig.getEnvFlag());
    }
}
