package com.rafay.Notification_Service.listener;

import static org.junit.Assert.*;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import org.testcontainers.shaded.org.awaitility.core.ConditionTimeoutException;

// import org.testcontainers.shaded.org.awaitility.core.ConditionTimeoutException;
// import static org.junit.jupiter.api.Assertions.assertThrows;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringSerializer;
import com.rafay.Notification_Service.controller.UserServiceClient;
import com.rafay.Notification_Service.dto.MatchEventDto;
import com.rafay.Notification_Service.dto.UserDto;
import com.rafay.Notification_Service.dto.UserServiceClientDto;
import com.rafay.Notification_Service.service.EmailService;
import com.rafay.Notification_Service.testConfig.ContainerInfo;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import static org.assertj.core.api.Assertions.assertThat;

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
    private Consumer<String, String> buildTestConsumer() {
    Map<String, Object> config = new HashMap<>();
    config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
    config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    config.put(ConsumerConfig.GROUP_ID_CONFIG, "test-dlt-consumer-" + System.currentTimeMillis());
    config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    return new DefaultKafkaConsumerFactory<String, String>(config).createConsumer();
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

        Awaitility.await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> {
                verify(emailService).sendMatchEmail("swiperEmail@test.com", "swiperUsername", "swipedUsername");
                verify(emailService).sendMatchEmail("swipedEmail@test.com", "swipedUsername", "swiperUsername");
        });

    }

    @Test
    void testListenOnKafkaRetryQueue2() throws Exception {
        kafkaTemplate = buildTestProducer();

        MatchEventDto matchEventDto = new MatchEventDto("swiperId3", "swipedId3");
        String jsonres = objectMapper.writeValueAsString(matchEventDto); // convert to JSON string
        //mock the userServiceClient to return UserDto objects for the given swiperId and swipedId
        when(userServiceClient.getUserById(new UserServiceClientDto(matchEventDto.swiperId())))
            .thenThrow(new RuntimeException("fail on original"))
            .thenThrow(new RuntimeException("fail on retry 1"))
            .thenReturn(new UserDto("2swiperEmail@test.com", "swiperUsername"));

        when(userServiceClient.getUserById(new UserServiceClientDto(matchEventDto.swipedId())))
            .thenReturn(new UserDto("2swipedEmail@test.com", "swipedUsername"));
        // stub producer
                kafkaTemplate.send(topic, jsonres);

        Awaitility.await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> {
                verify(emailService).sendMatchEmail("2swiperEmail@test.com", "swiperUsername", "swipedUsername");
                verify(emailService).sendMatchEmail("2swipedEmail@test.com", "swipedUsername", "swiperUsername");
        });

    }

    
    @Test
    void testListenOnKafkaRetryQueue3() throws Exception {
        kafkaTemplate = buildTestProducer();

        MatchEventDto matchEventDto = new MatchEventDto("swiperId", "swipedId");
        String jsonres = objectMapper.writeValueAsString(matchEventDto); // convert to JSON string
        //mock the userServiceClient to return UserDto objects for the given swiperId and swipedId

        when(userServiceClient.getUserById(new UserServiceClientDto(matchEventDto.swiperId())))
            .thenThrow(new RuntimeException("fail on original"))
            .thenThrow(new RuntimeException("fail on retry 1"))
            .thenThrow(new RuntimeException("fail on retry 2"))
            .thenReturn(new UserDto("swiperEmail@test.com", "swiperUsername"));

        when(userServiceClient.getUserById(new UserServiceClientDto(matchEventDto.swipedId())))
            .thenReturn(new UserDto("swipedEmail@test.com", "swipedUsername"));
        // stub producer
                kafkaTemplate.send(topic, jsonres);

        Awaitility.await().atMost(Duration.ofSeconds(45)).untilAsserted(() -> {
                verify(emailService).sendMatchEmail("swiperEmail@test.com", "swiperUsername", "swipedUsername");
                verify(emailService).sendMatchEmail("swipedEmail@test.com", "swipedUsername", "swiperUsername");
        });

    }


    @Test
    void testListenOnKafkaRetryQueue4ShouldNotWork() throws Exception {
        kafkaTemplate = buildTestProducer();

        MatchEventDto matchEventDto = new MatchEventDto("swiperId", "swipedId");
        String jsonres = objectMapper.writeValueAsString(matchEventDto); // convert to JSON string
        //mock the userServiceClient to return UserDto objects for the given swiperId and swipedId

        when(userServiceClient.getUserById(new UserServiceClientDto(matchEventDto.swiperId())))
            .thenThrow(new RuntimeException("fail on original"))
            .thenThrow(new RuntimeException("fail on retry 1"))
            .thenThrow(new RuntimeException("fail on retry 2"))
            .thenThrow(new RuntimeException("fail on retry 3"))
            .thenReturn(new UserDto("swiperEmail@test.com", "swiperUsername"));

        when(userServiceClient.getUserById(new UserServiceClientDto(matchEventDto.swipedId())))
            .thenReturn(new UserDto("swipedEmail@test.com", "swipedUsername"));
        // stub producer
                kafkaTemplate.send(topic, jsonres);

    assertThrows(ConditionTimeoutException.class, () -> {
        Awaitility.await().atMost(Duration.ofSeconds(45)).untilAsserted(() -> {
            verify(emailService).sendMatchEmail("swiperEmail@test.com", "swiperUsername", "swipedUsername");
        });
    });
    }



    @Test
void testListenOnKafkaRetryQueue4_ExceedsAttempts_GoesToDlt() throws Exception {
    kafkaTemplate = buildTestProducer();

    MatchEventDto matchEventDto = new MatchEventDto("swiperIdDLT", "swipedIdDLT");
    String jsonres = objectMapper.writeValueAsString(matchEventDto);

    // fail on all 4 real attempts — 5th stub is deliberately unreachable, proves the point
    when(userServiceClient.getUserById(new UserServiceClientDto(matchEventDto.swiperId())))
        .thenThrow(new RuntimeException("fail on original"))
        .thenThrow(new RuntimeException("fail on retry 1"))
        .thenThrow(new RuntimeException("fail on retry 2"))
        .thenThrow(new RuntimeException("fail on retry 3"))
        .thenReturn(new UserDto("swiperEmail@test.com", "swiperUsername")); // never reached

    when(userServiceClient.getUserById(new UserServiceClientDto(matchEventDto.swipedId())))
        .thenReturn(new UserDto("swipedEmail@test.com", "swipedUsername"));

    kafkaTemplate.send(topic, jsonres);

    // PROOF #1: no matter how long we wait, the 5th call (success) never comes
    assertThrows(ConditionTimeoutException.class, () -> {
        Awaitility.await().atMost(Duration.ofSeconds(45)).untilAsserted(() -> {
            verify(emailService).sendMatchEmail("swiperEmail@test.com", "swiperUsername", "swipedUsername");
        });
    });

    // PROOF #2: confirm the exact failed message landed on the DLT topic
    Consumer<String, String> dltConsumer = buildTestConsumer();
    dltConsumer.subscribe(Collections.singletonList(topic + "-dlt"));

    Awaitility.await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
        ConsumerRecords<String, String> records = KafkaTestUtils.getRecords(dltConsumer, Duration.ofSeconds(1));
        assertThat(records.count()).isEqualTo(1);

        String dltPayload = records.iterator().next().value();
        MatchEventDto dltEvent = objectMapper.readValue(dltPayload, MatchEventDto.class);

        assertThat(dltEvent.swiperId()).isEqualTo("swiperIdDLT");
        assertThat(dltEvent.swipedId()).isEqualTo("swipedIdDLT");
    });

    dltConsumer.close();
}

}
