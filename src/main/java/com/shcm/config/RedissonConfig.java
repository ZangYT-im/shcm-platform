package com.shcm.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * description
 * </p>
 *
 * @author ZangYT
 * @since 2023/11/14 1:29
 */

@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient(){
        // 配置
        Config config = new Config();
        config.useSingleServer().setAddress("redis://123.56.92.162:6379")
                .setPassword("123456");
        // 创建RedissonClient对象
        return Redisson.create(config);
    }
}
