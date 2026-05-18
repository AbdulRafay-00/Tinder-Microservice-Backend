package com.rafay.user_service.service.NearbySearchService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import tools.jackson.databind.ObjectMapper;

import com.rafay.user_service.Kafka.LocationMicroServiceKafkaTopic.KafkaNearbySearchProducer;
import com.rafay.user_service.dto.locationDto.NearbySearchEventDto;
import com.rafay.user_service.dto.locationDto.NearbySearchRequestDto;

@Service
public class NearbySearchProducer {

    private static final String TOPIC_NAME = "nearby-search";
    @Autowired
    KafkaNearbySearchProducer nearbySearchProducer;

    public NearbySearchEventDto publishNearbySearch(String userId, NearbySearchRequestDto nearbySearchRequestDto) {

        NearbySearchEventDto nearbySearchEventDto = new NearbySearchEventDto(
            userId,
            nearbySearchRequestDto.getLatitude(),
            nearbySearchRequestDto.getLongitude()
        );

        nearbySearchProducer.NearbySuggestion(TOPIC_NAME, nearbySearchEventDto);


        return nearbySearchEventDto;
    }
}