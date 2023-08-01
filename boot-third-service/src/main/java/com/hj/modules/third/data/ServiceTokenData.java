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
     * ������ʹ�� ��ȡ�����token
     *
     * @return token
     */
    public String getServiceTokenByPort() {
        return redisTemplate.opsForValue().get(BOOT_SERVICE_TOKEN_PREFIX.concat(port));
    }

    /**
     * ��bootʹ�� ���ݷ������ƻ�ȡtoken
     *
     * @param serviceName ��������
     * @return token
     */
    public String getServiceTokenByName(String serviceName) {
        return redisTemplate.opsForValue().get(BOOT_SERVICE_TOKEN_PREFIX.concat(serviceName));
    }

    /**
     * �����Լ���ʱ��token ���õ�������
     *
     * @param serviceName ��������
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
