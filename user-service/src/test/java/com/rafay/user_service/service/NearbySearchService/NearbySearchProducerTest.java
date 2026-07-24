package com.rafay.user_service.service.NearbySearchService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rafay.user_service.Kafka.LocationMicroServiceKafkaTopic.KafkaNearbySearchProducer;
import com.rafay.user_service.dto.locationDto.NearbySearchEventDto;
import com.rafay.user_service.dto.locationDto.NearbySearchRequestDto;

@ExtendWith(MockitoExtension.class)
public class NearbySearchProducerTest {

    @Mock
    KafkaNearbySearchProducer kafkaNearbySearchProducer;

    @InjectMocks
    NearbySearchProducer nearbySearchProducer;

    @Captor
    ArgumentCaptor<NearbySearchEventDto> eventCaptor;

    @Test
    void testPublishNearbySearch() {
        // Arrange
        String userId = "user-123";
        BigDecimal latitude = new BigDecimal("24.8607");
        BigDecimal longitude = new BigDecimal("67.0011");
        NearbySearchRequestDto requestDto = new NearbySearchRequestDto();
        requestDto.setLatitude(latitude);
        requestDto.setLongitude(longitude);

        // Act
        NearbySearchEventDto result = nearbySearchProducer.publishNearbySearch(userId, requestDto);

        // Assert - returned DTO built correctly
        assertEquals(userId, result.getUserId());
        assertEquals(0, latitude.compareTo(result.getLatitude()));
        assertEquals(0, longitude.compareTo(result.getLongitude()));

        // Assert - Kafka producer called with correct topic and captured event
        verify(kafkaNearbySearchProducer).NearbySuggestion(eq("nearby-search"), eventCaptor.capture());

        NearbySearchEventDto captured = eventCaptor.getValue();
        assertEquals(userId, captured.getUserId());
        assertEquals(0, latitude.compareTo(captured.getLatitude()));
        assertEquals(0, longitude.compareTo(captured.getLongitude()));
    }
}