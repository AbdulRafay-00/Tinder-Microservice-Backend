package com.rafay.Notification_Service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;
import software.amazon.awssdk.services.sesv2.model.SendEmailResponse;
import software.amazon.awssdk.services.sesv2.model.SesV2Exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private SesV2Client sesV2Client;

    private EmailService emailService;

    private final String senderEmail = "no-reply@tinderclone.com";

    @BeforeEach
    void setUp() {
        // constructor injection, so no @InjectMocks needed here —
        // we build it manually since senderEmail isn't a mock
        emailService = new EmailService(sesV2Client, senderEmail);
    }

    @Test
    void sendMatchEmail_success_sendsEmailWithCorrectContent() {
        // arrange
        when(sesV2Client.sendEmail(any(SendEmailRequest.class)))
                .thenReturn(SendEmailResponse.builder().messageId("test-message-id").build());

        // act
        emailService.sendMatchEmail("rafay@example.com", "Rafay", "Sara");

        // assert
        ArgumentCaptor<SendEmailRequest> captor = ArgumentCaptor.forClass(SendEmailRequest.class);
        verify(sesV2Client, times(1)).sendEmail(captor.capture());

        SendEmailRequest sentRequest = captor.getValue();

        assertThat(sentRequest.fromEmailAddress()).isEqualTo(senderEmail);
        assertThat(sentRequest.destination().toAddresses()).containsExactly("rafay@example.com");
        assertThat(sentRequest.content().simple().subject().data())
                .isEqualTo("🎉 You have a new match on Tinder!");

        String htmlBody = sentRequest.content().simple().body().html().data();
        assertThat(htmlBody).contains("Rafay");
        assertThat(htmlBody).contains("Sara");
        assertThat(htmlBody).contains("It's a Match!");
    }

    @Test
    void sendMatchEmail_sesException_isRethrown() {
        // arrange
        SesV2Exception sesException = (SesV2Exception) SesV2Exception.builder()
                .message("Email address not verified")
                .build();

        when(sesV2Client.sendEmail(any(SendEmailRequest.class)))
                .thenThrow(sesException);

        // act + assert
        SesV2Exception thrown = assertThrows(SesV2Exception.class,
                () -> emailService.sendMatchEmail("bad@example.com", "Rafay", "Sara"));

        assertThat(thrown.getMessage()).isEqualTo("Email address not verified");
        verify(sesV2Client, times(1)).sendEmail(any(SendEmailRequest.class));
    }

    @Test
    void sendMatchEmail_unexpectedException_isRethrown() {
        // arrange
        when(sesV2Client.sendEmail(any(SendEmailRequest.class)))
                .thenThrow(new RuntimeException("Network timeout"));

        // act + assert
        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> emailService.sendMatchEmail("rafay@example.com", "Rafay", "Sara"));

        assertThat(thrown.getMessage()).isEqualTo("Network timeout");
        verify(sesV2Client, times(1)).sendEmail(any(SendEmailRequest.class));
    }
}