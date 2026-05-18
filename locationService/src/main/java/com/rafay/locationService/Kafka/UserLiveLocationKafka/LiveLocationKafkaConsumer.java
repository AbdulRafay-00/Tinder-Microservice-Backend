package com.rafay.locationService.Kafka.UserLiveLocationKafka;

import com.rafay.locationService.DTO.NearbySearchEventDto;
import com.rafay.locationService.DTO.NearbySearchResultDto;
import com.rafay.locationService.Kafka.NearbySearchSuggestion.NearbySearchKafkaProducer;
import com.rafay.locationService.Service.locationService.LocationService;

import tools.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class LiveLocationKafkaConsumer {

    @Autowired
    private LocationService locationService;

    @Autowired
    private NearbySearchKafkaProducer nearbySearchKafkaProducer;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(
        topics = "nearby-search",
        groupId = "location-service-group"
    )
    public void consumeNearbySearch(String message) {
        try {
            NearbySearchEventDto event = objectMapper
                .readValue(message, NearbySearchEventDto.class);

            System.out.println("📨 Received: " + event.getUserId());

            List<String> nearbyUserIds = locationService.getRecommendations(
                event.getUserId(),
                event.getLatitude(),
                event.getLongitude()
            );

            System.out.println("✅ Nearby users found: " + nearbyUserIds.size());
            System.out.println(nearbyUserIds);

            NearbySearchResultDto result = new NearbySearchResultDto(
                event.getUserId(),
                nearbyUserIds
            );

            nearbySearchKafkaProducer.publishNearbyResult(result);

        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }
}