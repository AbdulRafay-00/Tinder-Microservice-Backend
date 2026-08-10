package com.rafay.match_service.testConfig;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;

import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
public abstract class it {

    @Container
    @ServiceConnection
    public static MySQLContainer<?> mysql = new MySQLContainer<>("8.0.43");
    
}
