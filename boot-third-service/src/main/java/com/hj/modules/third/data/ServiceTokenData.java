package com.hj.modules.third.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Component
public class ServiceTokenData {

    private final static String BOOT_SERVICE_TOKEN_PREFIX = "BOOT:SERVICE:TOKEN:";

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Value("${server.port}")
    private String port;

    /**
     * 给服务使用 获取服务的token
     *
     * @return token
     */
    public String getServiceTokenByPort() {
        return redisTemplate.opsForValue().get(BOOT_SERVICE_TOKEN_PREFIX.concat(port));
    }

    /**
     * 给boot使用 根据服务名称获取token
     *
     * @param serviceName 服务名称
     * @return token
     */
    public String getServiceTokenByName(String serviceName) {
        return redisTemplate.opsForValue().get(BOOT_SERVICE_TOKEN_PREFIX.concat(serviceName));
    }

    /**
     * 服务自己定时将token 设置到缓存中
     *
     * @param serviceName 服务名称
     */
    public void setServiceTokenByName(String serviceName) {
        redisTemplate.opsForValue().set(BOOT_SERVICE_TOKEN_PREFIX.concat(serviceName), getServiceTokenByPort(), 120, TimeUnit.SECONDS);
    }

    @Autowired(required = false)
    public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
        RedisSerializer<String> stringSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(stringSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(stringSerializer);
        this.redisTemplate = redisTemplate;
    }

}
