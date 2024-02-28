package com.shcm.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * <p>
 * description
 * </p>
 *
 * @author ZangYT
 * @since: 2024/2/25 19:24
 */

@Configuration
public class EnvConfig {

    static String envFlagCopy;
    @Value("${env.flag}")
    private String envFlag;

    public static String getEnvFlag() {
        return envFlagCopy;
    }

    @PostConstruct
    public void init() {
        envFlagCopy = this.envFlag;
    }
}
