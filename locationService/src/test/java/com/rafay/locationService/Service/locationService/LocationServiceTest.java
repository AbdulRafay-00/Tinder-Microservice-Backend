package com.rafay.locationService.Service.locationService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.configuration.injection.MockInjection;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;

import com.rafay.locationService.db_entries.LiveLocationDB;
import com.rafay.locationService.repository.LocationRepository;

import org.springframework.data.redis.connection.RedisGeoCommands;


@ExtendWith(MockitoExtension.class)
public class LocationServiceTest {

    @Mock
    RedisTemplate<String, String> redisTemplate;
    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private LocationService locationService;

    final String GEO_KEY = "fake_key";
    final String CACHE_PREFIX = "recommendations:";
    @Mock
    GeoOperations<String, String> geoOperations;
    @Mock
    ListOperations<String, String> listOperations;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForGeo()).thenReturn(geoOperations);
        lenient().when(redisTemplate.opsForList()).thenReturn(listOperations);
    }

    // @Test
    // void testLoadAllLocationsIntoRedis() {

    // }

    @Test
    void testGetRecommendationsCase_CachePresent() {
        String userId = "user1";
        BigDecimal latitude = new BigDecimal("37.7749");
        BigDecimal longitude = new BigDecimal("-122.4194");

        when(redisTemplate.opsForList().range(CACHE_PREFIX + userId, 0, -1))
                .thenReturn(Arrays.asList("loc1", "loc2"));

        List<String> result = locationService.getRecommendations(userId, latitude, longitude);

        assertEquals(Arrays.asList("loc1", "loc2"), result);
        verify(listOperations, times(1)).range(CACHE_PREFIX + userId, 0, -1);

    }


    @Test
void testGetRecommendationsCase_CacheMiss_LocationChanged() {
    String userId = "user1";
    BigDecimal newLat = new BigDecimal("37.7749");
    BigDecimal newLng = new BigDecimal("-122.4194");

    // STEP 1: Cache is empty -> forces cache miss branch
    when(listOperations.range(CACHE_PREFIX + userId, 0, -1))
        .thenReturn(Collections.emptyList());

    // STEP 2: Existing DB record has a DIFFERENT location -> "location changed" branch
    LiveLocationDB existing = new LiveLocationDB(
        new BigDecimal("40.0000"), new BigDecimal("-120.0000"));
    existing.setUserId(userId);
    when(locationRepository.findById(userId)).thenReturn(Optional.of(existing));

    // STEP 3: GEORADIUS returns one nearby user (not yourself)
    Point point = new Point(newLng.doubleValue(), newLat.doubleValue());
    RedisGeoCommands.GeoLocation<String> geoLoc =
        new RedisGeoCommands.GeoLocation<>("user2", point);
    GeoResult<RedisGeoCommands.GeoLocation<String>> geoResult =
        new GeoResult<>(geoLoc, new Distance(2.0));
    GeoResults<RedisGeoCommands.GeoLocation<String>> geoResults =
        new GeoResults<>(List.of(geoResult));

    when(geoOperations.radius(eq("user_locations"), any(Circle.class)))
        .thenReturn(geoResults);

    // ACT
    List<String> result = locationService.getRecommendations(userId, newLat, newLng);

    // ASSERT: correct result
    assertEquals(List.of("user2"), result);

    // ASSERT: location changed -> DB save happened
    verify(locationRepository, times(1)).save(any(LiveLocationDB.class));

    // ASSERT: location changed -> Redis GEO updated
    verify(geoOperations, times(1)).add(eq("user_locations"), any(Point.class), eq(userId));

    // ASSERT: old cache deleted since location changed
    verify(redisTemplate, times(1)).delete(CACHE_PREFIX + userId);

    // ASSERT: fresh results got cached with TTL
    verify(listOperations, times(1)).rightPushAll(CACHE_PREFIX + userId, List.of("user2"));
    verify(redisTemplate, times(1)).expire(CACHE_PREFIX + userId, Duration.ofMinutes(15));
}

}