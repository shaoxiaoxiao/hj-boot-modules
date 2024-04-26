package com.hj.modules.log.data;

import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class UserData {

    private static final Logger log = LoggerFactory.getLogger(UserData.class);
    private final static String ADMIN_ONLINE_USER_PREFIX = "BOOT:ADMIN:ONLINE_USER:";

    private static UserData userData;

    @Autowired
    private RedissonClient redissonClient;

    private RMapCache<String, UserInfo> tokenUserInRedis;


    @PostConstruct
    public void init() {
        userData = this;
        tokenUserInRedis = redissonClient.getMapCache(ADMIN_ONLINE_USER_PREFIX, new TypedJsonJacksonCodec(String.class, UserInfo.class));
    }

    public static UserInfo getUserInfo(String token) {
        if (StringUtils.isEmpty(token)) {
//            log.warn("非法的token进行用户信息获取");
            return null;
        }
        UserInfo userInfo = userData.tokenUserInRedis.get(token);
        if (null == userInfo) {
//            log.warn("非法的token进行用户信息获取");
            return null;
        }
        return userInfo;
    }
}
