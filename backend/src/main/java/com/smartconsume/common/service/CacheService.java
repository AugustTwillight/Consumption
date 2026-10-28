package com.smartconsume.common.service;

import com.smartconsume.common.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Redis 缓存服务类
 */
@Slf4j
@Service
public class CacheService {

    private final RedisService redisService;

    public CacheService(RedisService redisService) {
        this.redisService = redisService;
    }

    /**
     * 设置缓存
     *
     * @param key      缓存键
     * @param value    缓存值
     * @param expireTime 过期时间（秒）
     */
    public void set(String key, Object value, long expireTime) {
        redisService.setEx(key, value, expireTime, TimeUnit.SECONDS);
    }

    /**
     * 获取缓存
     *
     * @param key 缓存键
     * @return 缓存值
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        Object value = redisService.get(key);
        if (value != null && clazz.isInstance(value)) {
            return (T) value;
        }
        return null;
    }

    /**
     * 删除缓存
     *
     * @param key 缓存键
     */
    public void delete(String key) {
        redisService.delete(key);
    }

    /**
     * 判断缓存是否存在
     *
     * @param key 缓存键
     * @return 是否存在
     */
    public boolean exists(String key) {
        return redisService.hasKey(key);
    }
}
