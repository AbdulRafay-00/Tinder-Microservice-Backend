// package com.rafay.locationService.Service.locationService;
// import com.rafay.locationService.db_entries.LiveLocationDB;
// import com.rafay.locationService.repository.LocationRepository;
// import jakarta.annotation.PostConstruct;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.geo.*;
// import org.springframework.data.redis.connection.RedisGeoCommands;
// import org.springframework.data.redis.core.GeoOperations;
// import org.springframework.data.redis.core.RedisTemplate;
// import org.springframework.stereotype.Service;

// import java.math.BigDecimal;
// import java.time.Duration;
// import java.util.Collections;
// import java.util.List;

// @Service
// public class LocationService {

//     private static final String GEO_KEY = "user_locations";
//     private static final String CACHE_PREFIX = "recommendations:";

//     @Autowired
//     private RedisTemplate<String, String> redisTemplate;

//     @Autowired
//     private LocationRepository locationRepository;

//     // Runs once on startup — loads all MySQL data into Redis GEO
//     @PostConstruct
//     public void loadAllLocationsIntoRedis() {
//         GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();
//         List<LiveLocationDB> allLocations = locationRepository.findAll();

//         for (LiveLocationDB loc : allLocations) {
//             geoOps.add(
//                 GEO_KEY,
//                 new Point(
//                     loc.getLongitude().doubleValue(),
//                     loc.getLatitude().doubleValue()
//                 ),
//                 loc.getUserId()
//             );
//         }
//         System.out.println("✅ Redis GEO loaded with " 
//             + allLocations.size() + " users");
//     }

//     public List<String> getRecommendations(
//             String userId,
//             BigDecimal latitude,
//             BigDecimal longitude) {

//         String cacheKey = CACHE_PREFIX + userId;

//         // STEP 1: Check per-person cache in Redis
//         List<String> cached = redisTemplate
//             .opsForList()
//             .range(cacheKey, 0, -1);

//         if (cached != null && !cached.isEmpty()) {
//             System.out.println("✅ Cache HIT for: " + userId);
//             return cached;
//         }

//         System.out.println("❌ Cache MISS for: " + userId);

//         // STEP 2: UPSERT into MySQL
//         LiveLocationDB location = new LiveLocationDB(latitude, longitude);
//         location.setUserId(userId);
//         locationRepository.save(location);

//         // STEP 3: Update Redis GEO with latest coordinates
//         GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();
//         geoOps.add(
//             GEO_KEY,
//             new Point(
//                 longitude.doubleValue(),
//                 latitude.doubleValue()
//             ),
//             userId
//         );

//         // STEP 4: GEORADIUS — find everyone within 10km
//         GeoResults<RedisGeoCommands.GeoLocation<String>> results =
//             geoOps.radius(
//                 GEO_KEY,
//                 new Circle(
//                     new Point(
//                         longitude.doubleValue(),
//                         latitude.doubleValue()
//                     ),
//                     new Distance(10, Metrics.KILOMETERS)
//                 )
//             );

//         // STEP 5: Extract ids, remove yourself
//         if (results == null) {
//             return Collections.emptyList();
//         }

//         List<String> nearbyUserIds = results.getContent()
//             .stream()
//             .map(r -> r.getContent().getName())
//             .filter(id -> !id.equals(userId))
//             .toList();

//         // STEP 6: Cache result per person with 15 min TTL
//         if (!nearbyUserIds.isEmpty()) {
//             redisTemplate.opsForList()
//                 .rightPushAll(cacheKey, nearbyUserIds);
//             redisTemplate.expire(
//                 cacheKey, 
//                 Duration.ofMinutes(15)
//             );
//         }

//         return nearbyUserIds;
//     }
// }

// redis implementation with per-person caching and location change detection


package com.rafay.locationService.Service.locationService;
import com.rafay.locationService.db_entries.LiveLocationDB;
import com.rafay.locationService.repository.LocationRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Service
public class LocationService {

    private static final Logger log = LoggerFactory.getLogger(LocationService.class);
    private static final String GEO_KEY = "user_locations";
    private static final String CACHE_PREFIX = "recommendations:";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private LocationRepository locationRepository;

    @PostConstruct
    public void loadAllLocationsIntoRedis() { // ---1

        Long existingCount = redisTemplate.opsForZSet().size(GEO_KEY);

        if (existingCount != null && existingCount > 0) {
            log.info("Redis GEO already warm ({} users) — skipping MySQL bootstrap", existingCount);
            return;
        }

        log.warn("Redis GEO empty — bootstrapping from MySQL");

        GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();
        List<LiveLocationDB> allLocations = locationRepository.findAll();

        for (LiveLocationDB loc : allLocations) {
            geoOps.add(
                GEO_KEY,
                new Point(
                    loc.getLongitude().doubleValue(),
                    loc.getLatitude().doubleValue()
                ),
                loc.getUserId()
            );
        }
        log.info("✅ Redis GEO loaded with {} users", allLocations.size());
    }// ---1



    public List<String> getRecommendations(
            String userId,
            BigDecimal latitude,
            BigDecimal longitude) { // ---2

        String cacheKey = CACHE_PREFIX + userId;

        // STEP 1: Check per-person cache in Redis
        List<String> cached = redisTemplate
            .opsForList()
            .range(cacheKey, 0, -1);

        if (cached != null && !cached.isEmpty()) {
            log.info("✅ Cache HIT for: {}", userId);
            return cached;
        }

        log.info("❌ Cache MISS for: {}", userId);

        // STEP 2: Check if location actually changed
        LiveLocationDB existing = locationRepository
            .findById(userId)
            .orElse(null);

        boolean locationChanged = existing == null
            || existing.getLatitude().compareTo(latitude) != 0
            || existing.getLongitude().compareTo(longitude) != 0;

        if (locationChanged) {
            System.out.println("📍 Location changed for: " + userId);

            // STEP 3a: Update MySQL only if location changed
            LiveLocationDB location = new LiveLocationDB(latitude, longitude);
            location.setUserId(userId);
            locationRepository.save(location);

            // STEP 3b: Update Redis GEO only if location changed
            GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();
            geoOps.add(
                GEO_KEY,
                new Point(
                    longitude.doubleValue(),
                    latitude.doubleValue()
                ),
                userId
            );

            // STEP 3c: Delete old cache since location changed
            redisTemplate.delete(cacheKey);
            System.out.println("🗑️ Old cache deleted for: " + userId);

        } else {
            System.out.println("📍 Location unchanged for: " + userId);
        }

        // STEP 4: GEORADIUS — find everyone within 10km
        GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();
        GeoResults<RedisGeoCommands.GeoLocation<String>> results =
            geoOps.radius(
                GEO_KEY,
                new Circle(
                    new Point(
                        longitude.doubleValue(),
                        latitude.doubleValue()
                    ),
                    new Distance(10, Metrics.KILOMETERS)
                )
            );

        // STEP 5: Extract ids, remove yourself
        if (results == null) {
            return Collections.emptyList();
        }

        List<String> nearbyUserIds = results.getContent()
            .stream()
            .map(r -> r.getContent().getName())
            .filter(id -> !id.equals(userId))
            .toList();

        // STEP 6: Cache result per person with 15 min TTL
        if (!nearbyUserIds.isEmpty()) {
            redisTemplate.opsForList()
                .rightPushAll(cacheKey, nearbyUserIds);
            redisTemplate.expire(
                cacheKey,
                Duration.ofMinutes(15)
            );
        }

        return nearbyUserIds;
    } // ---2
}