package com.rafay.locationService.controller;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import com.rafay.locationService.DTO.LocationRequestDTO;
import com.rafay.locationService.Service.locationService.LocationService;
import com.rafay.locationService.db_entries.LiveLocationDB;
import com.rafay.locationService.repository.LocationRepository;
import com.rafay.locationService.testConfig.BaseIntegrationTest;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LocationControllerIT extends BaseIntegrationTest {
    @Autowired
    private LocationService locationService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    LocationRepository locationRepository;

    // @Autowired
    // private RedisTemplate<String, String> redisTemplate;

    // GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();

    // List<LiveLocationDB> allLocations = locationRepository.findAll();

    @Sql("/sql-scripts/Pre-Add-data.sql")
    @Test
    void redisCacheHit() throws Exception {
        locationService.loadAllLocationsIntoRedis();
        String request = """
                {
                    "userId": "test-user-1",
                    "latitude": 24.860,
                    "longitude": 67.001
                }
                """;

        mockMvc.perform(post("/location/nearby-search/sync")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)).andExpect(status().isOk());

        // Test logic for Redis cache hit

    }

    @Test
    @Sql("/sql-scripts/Pre-Add-data.sql")
    void redisCacheMiss() throws Exception {

        String request = """
                {
                    "userId": "test-user-1",
                    "latitude": 24.860,
                    "longitude": 67.200
                }
                """;

        mockMvc.perform(
                post("/location/nearby-search/sync")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk());
    }

}
