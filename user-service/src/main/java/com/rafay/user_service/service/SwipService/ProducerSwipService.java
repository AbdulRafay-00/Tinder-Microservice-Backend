package com.rafay.user_service.service.SwipService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.rafay.user_service.Kafka.KafkaProducer;
import com.rafay.user_service.dto.swipLogicDto.SwipEventFrontendDto;
import com.rafay.user_service.dto.swipLogicDto.SwipEventKafkaProduce;
import org.springframework.stereotype.Service;

@Service
public class ProducerSwipService {

    @Autowired
    private KafkaProducer kafkaProducer;
    public SwipEventKafkaProduce processSwipe(String swiperId, SwipEventFrontendDto frontendDto) {

        // map frontend dto + jwt swiperId into kafka dto
        SwipEventKafkaProduce kafkaEvent = new SwipEventKafkaProduce(
            swiperId,                       // from JWT
            frontendDto.getSwipedId(),      // from request body
            frontendDto.getSwipeDirection() // from request body
        );

        kafkaProducer.kafkaMessage("swipe-events", kafkaEvent);

        return kafkaEvent;
    }
}

