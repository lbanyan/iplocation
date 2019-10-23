package com.huya.iplocation.component;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * 健康检查
 *
 * @author xingping
 * @date 2019-8-24
 */
@Component
public class HealthCheck implements HealthIndicator {

    @Override
    public Health health() {

        return new Health.Builder().up().build();
    }
}
