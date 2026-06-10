package com.rafay.Notification_Service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sesv2.SesV2Client;


@Configuration
public class SesConfig {

    @Value("${aws.access-key}")
private String accessKeyId;

@Value("${aws.secret-key}")
private String secretKey;

@Value("${aws.region}")
private String region;
     @Bean
    public SesV2Client sesV2Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKeyId, secretKey);
        return SesV2Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }
}
