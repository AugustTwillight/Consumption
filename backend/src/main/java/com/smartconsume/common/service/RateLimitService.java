package com.smartconsume.common.service;

import com.smartconsume.common.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 限流服务类
 */
@Slf4j
@Service
public class RateLimitService {

    private final RedisService redisService;

    public RateLimitService(RedisService redisService) {
        this.redisService = redisService;
    }

    /**
     * 检查是否超过限流阈值
     *
     * @param key        限流键（如：login:fail:username 或 calc:user:123）
     * @param maxCount   最大请求次数
     * @param expireTime 过期时间（秒）
     * @return true 表示超过限流或已达到阈值，false 表示允许请求
     */
    public boolean isLimit(String key, int maxCount, int expireTime) {
        String countKey = "rate_limit:" + key;
        Long count = redisService.increment(countKey);
        // 如果是第一次计数，设置过期时间
        if (count != null && count == 1) {
            redisService.expire(countKey, expireTime, java.util.concurrent.TimeUnit.SECONDS);
        }
        // 超过最大次数则返回 true（限流）
        return count != null && count > maxCount;
    }

    /**
     * 重置限流计数
     *
     * @param key 限流键
     */
    public void resetLimit(String key) {
        String countKey = "rate_limit:" + key;
        redisService.delete(countKey);
    }
}
