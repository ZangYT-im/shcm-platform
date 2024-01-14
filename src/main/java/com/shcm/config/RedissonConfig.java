package com.shcm.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
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


    @Value("${spring.redis.host}")
    private String redisAddress;

    @Value("${spring.redis.password}")
    private String redisPass;

    @Bean
    public RedissonClient redissonClient(){
        // 配置
        Config config = new Config();
        config.useSingleServer().setAddress("redis://"+redisAddress+":6379")
                .setPassword(redisPass);
        // 创建RedissonClient对象
        return Redisson.create(config);
    }
}
