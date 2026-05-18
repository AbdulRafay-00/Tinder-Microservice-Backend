package com.rafay.user_service.Kafka;

// import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafay.user_service.dto.swipLogicDto.SwipEventKafkaProduce;

import tools.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void kafkaMessage(String topic, SwipEventKafkaProduce swipEvent) {
        try {
            String jsonstr = objectMapper.writeValueAsString(swipEvent); // DTO → JSON
            kafkaTemplate.send(topic, jsonstr);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize swipe event", e);
        }
    }
}





// raw dto approach problem will send garbage data if not converted to json string before sending to kafka topic, hence not used in final code
// package com.rafay.user_service.Kafka;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.kafka.core.KafkaTemplate;
// import org.springframework.stereotype.Service;

// import com.rafay.user_service.dto.swipLogicDto.SwipEventKafkaProduce;

// @Service
// public class KafkaProducer {

//     @Autowired
//     private KafkaTemplate<String, SwipEventKafkaProduce> kafkaTemplate;

//     public void kafkaMessage(String topic, SwipEventKafkaProduce swipEvent) {
//         kafkaTemplate.send(topic, swipEvent);
//     }

// }
