package com.rafay.Orchestration_Service.Redis;

import java.time.Duration;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.rafay.Orchestration_Service.DTO.NearbySearchResultDto;

@Service
public class NearByUserCache {

    private static final String CACHE_KEY_PREFIX = "discovery:";
    private static final long CACHE_TTL_MINUTES = 20;

    private final RedisTemplate<String, String> redisTemplate;

    public NearByUserCache(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // store full list in Redis
    public void cacheNearbyUsers(String userId, List<String> userIds) {

        String key = buildCacheKey(userId);
        redisTemplate.opsForList().rightPushAll(key, userIds);
        redisTemplate.expire(key, Duration.ofMinutes(CACHE_TTL_MINUTES));
    }

    // get full list from Redis
    public List<String> getCachedNearbyUsers(String userId) {
        return redisTemplate.opsForList().range(buildCacheKey(userId), 0, -1);
    }

    // check if cache exists
    public boolean isCached(String userId) {
        List<String> cached = getCachedNearbyUsers(userId);
        return cached != null && !cached.isEmpty();
    }

    private String buildCacheKey(String userId) {
        return CACHE_KEY_PREFIX + userId;
    }
}


