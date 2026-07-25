package com.rafay.user_service.service.SwipService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rafay.user_service.Kafka.KafkaProducer;
import com.rafay.user_service.dto.swipLogicDto.SwipEventFrontendDto;
import com.rafay.user_service.dto.swipLogicDto.SwipEventKafkaProduce;
// import the enum's actual package here, e.g.:
// import com.rafay.user_service.dto.swipLogicDto.SwipeDirection;
import com.rafay.user_service.dto.swipLogicDto.SwipeDirection;

@ExtendWith(MockitoExtension.class)
public class ProducerSwipServiceTest {

    @Mock
    KafkaProducer kafkaProducer;

    @InjectMocks
    ProducerSwipService producerSwipService;

    @Captor
    ArgumentCaptor<SwipEventKafkaProduce> eventCaptor;

    @Test
    void testProcessSwipe_buildsEventAndSendsToKafka() {
        // Arrange
        String swiperId = "user-111";
        SwipEventFrontendDto frontendDto = new SwipEventFrontendDto();
        frontendDto.setSwipedId("user-222");
        frontendDto.setSwipeDirection(SwipeDirection.RIGHT); // enum constant, not a String

        // Act
        SwipEventKafkaProduce result = producerSwipService.processSwipe(swiperId, frontendDto);

        // Assert - returned DTO built correctly
        assertEquals(swiperId, result.getSwiperId());
        assertEquals("user-222", result.getSwipedId());
        assertEquals(SwipeDirection.RIGHT, result.getSwipeDirection());

        // Assert - Kafka producer called with correct topic and event contents
        verify(kafkaProducer).kafkaMessage(eq("swipe-events"), eventCaptor.capture());

        SwipEventKafkaProduce captured = eventCaptor.getValue();
        assertEquals(swiperId, captured.getSwiperId());
        assertEquals("user-222", captured.getSwipedId());
        assertEquals(SwipeDirection.RIGHT, captured.getSwipeDirection());
    }
}