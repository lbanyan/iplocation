package com.huya.iplocation.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 环境
 *
 * @author xingping
 * @date 2019-07-22
 */
@Component
public class Env {

    @Value("${spring.profiles.active}")
    private String env;

    public boolean isProd() {
        return "prod".equalsIgnoreCase(env);
    }

    public boolean isDev() {
        return "dev".equalsIgnoreCase(env);
    }

    public boolean isTest() {
        return "test".equalsIgnoreCase(env);
    }
}
