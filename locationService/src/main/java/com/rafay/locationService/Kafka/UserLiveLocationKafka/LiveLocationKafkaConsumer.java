package com.rafay.locationService.Kafka.UserLiveLocationKafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafay.locationService.DTO.NearbySearchEventDto;
import com.rafay.locationService.DTO.NearbySearchResultDto;
import com.rafay.locationService.Kafka.NearbySearchSuggestion.NearbySearchKafkaProducer;
import com.rafay.locationService.Service.locationService.NearbySearchService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class LiveLocationKafkaConsumer {

    @Autowired
    private NearbySearchService nearbySearchService;

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

            NearbySearchResultDto result = nearbySearchService.processNearbySearch(
                event.getUserId(),
                event.getLatitude(),
                event.getLongitude()
            );

            System.out.println("✅ Nearby users found: " + result.getNearbyUserIds().size());
            System.out.println(result.getNearbyUserIds());

            nearbySearchKafkaProducer.publishNearbyResult(result);

        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }
}