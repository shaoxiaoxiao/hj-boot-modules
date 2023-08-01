package com.hj.modules.third.data;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ServiceTokenData {

    private final static String BOOT_SERVICE_TOKEN_PREFIX = "BOOT:SERVICE:TOKEN:";

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Value("${server.port}")
    private String port;

    public String getToken() {
        return redisTemplate.opsForValue().get(BOOT_SERVICE_TOKEN_PREFIX.concat(port));
    }

}
