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
import software.amazon.awssdk.services.sesv2.model.SesV2Exception;
@Slf4j
@Service
public class EmailService {

    private final SesV2Client sesV2Client;
    private final String senderEmail;

    public EmailService(SesV2Client sesV2Client, @Value("${aws.ses.sender}") String senderEmail) {
        this.sesV2Client = sesV2Client;
        this.senderEmail = senderEmail;
    }
public void sendMatchEmail(String toEmail, String recipientName, String matchedWithName) {
    String subject = "🎉 You have a new match on Tinder!";
    String htmlBody = "<!DOCTYPE html><html><body style='margin:0; padding:0; background-color:#F0F2F5;'>"
        + "<table role='presentation' width='100%' cellpadding='0' cellspacing='0' style='background-color:#F0F2F5; padding:40px 0;'>"
        + "<tr><td align='center'>"
        + "<table role='presentation' width='480' cellpadding='0' cellspacing='0' style='background-color:#FFFFFF; border-radius:16px; overflow:hidden; box-shadow:0 4px 24px rgba(0,0,0,0.08); font-family:Helvetica, Arial, sans-serif;'>"

        // Header — warm coral/pink gradient = excitement, romance
        + "<tr><td align='center' style='background:linear-gradient(135deg, #FF6B6B 0%, #FF8FA3 100%); padding:40px 24px;'>"
        + "<div style='font-size:44px;'>💘</div>"
        + "<div style='color:#FFFFFF; font-size:26px; font-weight:700; margin-top:12px;'>It's a Match!</div>"
        + "</td></tr>"

        // Body
        + "<tr><td style='padding:32px 32px 8px 32px; text-align:center;'>"
        + "<p style='font-size:20px; color:#1F2937; margin:0 0 8px 0;'>Hi <b>" + recipientName + "</b>,</p>"
        + "<p style='font-size:16px; color:#4B5563; line-height:1.5; margin:0;'>You and <b style=\"color:#FF6B6B;\">" + matchedWithName + "</b> liked each other. Time to say hi 👋</p>"
        + "</td></tr>"

        // Trust badge — cool blue/teal = calm, security, trust
        + "<tr><td style='padding:24px 32px;'>"
        + "<table role='presentation' width='100%' cellpadding='0' cellspacing='0' style='background-color:#EAF6F6; border-radius:12px;'>"
        + "<tr><td style='padding:16px 20px;'>"
        + "<table role='presentation' cellpadding='0' cellspacing='0'><tr>"
        + "<td style='vertical-align:middle; padding-right:12px;'><div style='width:36px; height:36px; background-color:#2E9E9E; border-radius:50%; text-align:center; line-height:36px; font-size:18px; color:#FFFFFF;'>🛡️</div></td>"
        + "<td style='vertical-align:middle;'><div style='font-size:14px; font-weight:600; color:#1B6B6B;'>Safe to connect</div>"
        + "<div style='font-size:13px; color:#3F8080;'>This match went through our standard verification checks.</div></td>"
        + "</tr></table>"
        + "</td></tr></table>"
        + "</td></tr>"

        // CTA
        + "<tr><td align='center' style='padding:8px 32px 32px 32px;'>"
        + "<a href='#' style='display:inline-block; background-color:#FF6B6B; color:#FFFFFF; text-decoration:none; font-size:16px; font-weight:600; padding:14px 36px; border-radius:999px;'>Say Hello 💬</a>"
        + "</td></tr>"

        // Footer
        + "<tr><td align='center' style='padding:20px; background-color:#FAFAFA; border-top:1px solid #EEEEEE;'>"
        + "<p style='font-size:12px; color:#9CA3AF; margin:0;'>— The Tinder Team</p>"
        + "</td></tr>"

        + "</table></td></tr></table></body></html>";
        

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
        log.info("Match email sent to {}", toEmail);

    } catch (SesV2Exception e) {
        // AWS SES specific error
        log.error("SES failed to send email to {} — reason: {}", toEmail, e.getMessage());
        throw e; // rethrow — retry queue will catch this

    } catch (Exception e) {
        // any other unexpected error
        log.error("Unexpected error sending email to {}", toEmail, e);
        throw e; // rethrow — retry queue will catch this
    }
}
}