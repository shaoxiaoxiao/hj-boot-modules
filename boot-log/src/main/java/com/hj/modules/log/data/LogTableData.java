package com.hj.modules.log.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class LogTableData {

    private final static String BOOT_TABLE_NAME_PREFIX = "BOOT:TABLE:NAME:";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public String getTableName(String table) {
        return redisTemplate.opsForValue().get(BOOT_TABLE_NAME_PREFIX.concat(table));
    }

}
