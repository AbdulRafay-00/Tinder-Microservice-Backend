package com.rafay.Notification_Service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.Body;
import software.amazon.awssdk.services.sesv2.model.Content;
import software.amazon.awssdk.services.sesv2.model.Destination;
import software.amazon.awssdk.services.sesv2.model.EmailContent;
import software.amazon.awssdk.services.sesv2.model.Message;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;
@Slf4j
@Service
public class EmailService {

    private final SesV2Client sesV2Client;
    private final String senderEmail;

    public EmailService(SesV2Client sesV2Client, @Value("${aws.ses.sender}") String senderEmail) {
        this.sesV2Client = sesV2Client;
        this.senderEmail = senderEmail;
    }

    public void sendMatchEmail(String toEmail, String matchedWithName) {
        String subject = "You have a new match on Tinder!";
        String htmlBody = "<html><body><h1>You have a new match on Tinder!</h1>"
                + "<p>You matched with " + matchedWithName + ".</p></body></html>";

        try {
            SendEmailRequest request = SendEmailRequest.builder()
                    .fromEmailAddress(senderEmail)
                    .destination(Destination.builder().toAddresses(toEmail).build())
                    .content(EmailContent.builder()
                            .simple(Message.builder()
                                    .subject(Content.builder().data(subject).build())
                                    .body(Body.builder()
                                            .html(Content.builder().data(htmlBody).build())
                                            .build())
                                    .build())
                            .build())
                    .build();
            sesV2Client.sendEmail(request);
            log.info("Sent match email to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send match email to {}", toEmail, e);
            throw e;
        }
    }
}