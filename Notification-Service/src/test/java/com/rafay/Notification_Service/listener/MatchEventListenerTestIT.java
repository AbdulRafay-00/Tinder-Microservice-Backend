package com.rafay.Notification_Service.listener;

import static org.mockito.Mockito.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.StringSerializer;
import com.rafay.Notification_Service.controller.UserServiceClient;
import com.rafay.Notification_Service.dto.MatchEventDto;
import com.rafay.Notification_Service.dto.UserDto;
import com.rafay.Notification_Service.dto.UserServiceClientDto;
import com.rafay.Notification_Service.service.EmailService;
import com.rafay.Notification_Service.testConfig.ContainerInfo;

public class MatchEventListenerTestIT extends ContainerInfo {
    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    EmailService emailService;

    @MockitoBean
    private UserServiceClient userServiceClient;
    
    private KafkaTemplate<String, String> buildTestProducer() {
        Map<String, Object> config = new HashMap<>();
        config.put("bootstrap.servers", kafka.getBootstrapServers());
        config.put("key.serializer", StringSerializer.class);
        config.put("value.serializer", StringSerializer.class);
        ProducerFactory<String, String> pf = new DefaultKafkaProducerFactory<>(config);
        return new KafkaTemplate<>(pf);
    }
    
    @Value("${app.kafka.topics.match-topic}")
    String topic;


    @Test
    void testListenOnKafkaMainQueue() throws Exception {
        kafkaTemplate = buildTestProducer();

        MatchEventDto matchEventDto = new MatchEventDto("swiperId", "swipedId");
        String jsonres = objectMapper.writeValueAsString(matchEventDto); // convert to JSON string
        //mock the userServiceClient to return UserDto objects for the given swiperId and swipedId
        when(userServiceClient.getUserById(new UserServiceClientDto(matchEventDto.swiperId())))
        .thenReturn(new UserDto("swiperEmail@test.com", "swiperUsername"));
        when(userServiceClient.getUserById(new UserServiceClientDto(matchEventDto.swipedId())))
        .thenReturn(new UserDto("swipedEmail@test.com", "swipedUsername"));
        // stub producer
                kafkaTemplate.send(topic, jsonres);

        Awaitility.await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
                verify(emailService).sendMatchEmail("swiperEmail@test.com", "swiperUsername", "swipedUsername");
                verify(emailService).sendMatchEmail("swipedEmail@test.com", "swipedUsername", "swiperUsername");
        });}

// poll failed once from main kafka queue and then fetch from retry queue
@Test
    void testListenOnKafkaRetryQueue1() throws Exception {
        kafkaTemplate = buildTestProducer();

        MatchEventDto matchEventDto = new MatchEventDto("swiperId", "swipedId");
        String jsonres = objectMapper.writeValueAsString(matchEventDto); // convert to JSON string
        //mock the userServiceClient to return UserDto objects for the given swiperId and swipedId
        when(userServiceClient.getUserById(new UserServiceClientDto(matchEventDto.swiperId())))
            .thenThrow(new RuntimeException("fail on original"))
            .thenReturn(new UserDto("swiperEmail@test.com", "swiperUsername"));

        when(userServiceClient.getUserById(new UserServiceClientDto(matchEventDto.swipedId())))
            .thenThrow(new RuntimeException("fail on original"))
            .thenReturn(new UserDto("swipedEmail@test.com", "swipedUsername"));
        // stub producer
                kafkaTemplate.send(topic, jsonres);

        Awaitility.await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
                verify(emailService).sendMatchEmail("swiperEmail@test.com", "swiperUsername", "swipedUsername");
                verify(emailService).sendMatchEmail("swipedEmail@test.com", "swipedUsername", "swiperUsername");
        });

    }
}
