package com.hj.modules.third.data;

import cn.hutool.json.JSONUtil;
import com.hj.modules.third.bean.ServiceInterfaceUrlBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class ServiceUrlData {

    private final static String BOOT_SERVICE_URL_PREFIX = "BOOT:SERVICE:URL:";

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Value("${hj.third.service.name}")
    private String serviceName;

    public List<ServiceInterfaceUrlBean> getControllerUrlByName(String serviceName) {
        String jsonString = redisTemplate.opsForValue().get(BOOT_SERVICE_URL_PREFIX.concat(serviceName));
        return JSONUtil.toList(jsonString, ServiceInterfaceUrlBean.class);
    }

    public void setControllerUrlByName(List<ServiceInterfaceUrlBean> list) {
        String jsonString = JSONUtil.toJsonStr(list);
        redisTemplate.opsForValue().set(BOOT_SERVICE_URL_PREFIX.concat(serviceName), jsonString, 120, TimeUnit.SECONDS);
    }
}
