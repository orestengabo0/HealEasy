package org.healeasy.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.healeasy.Iservices.IEmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Implementation of the IEmailService interface.
 * Uses JavaMailSender to send emails.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements IEmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:orestengabo0@gmail.com}")
    private String fromEmail;

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    /**
     * Send a simple text email.
     * 
     * @param to The recipient's email address
     * @param subject The email subject
     * @param text The email body text
     * @return true if the email was sent successfully, false otherwise
     */
    @Override
    public boolean sendSimpleEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            log.info("Simple email sent to: {}", to);
            return true;
        } catch (Exception e) {
            log.error("Failed to send simple email to: {}. Error: {}", to, e.getMessage(), e);
            // Print stack trace to console for easier debugging
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Send an HTML email.
     * 
     * @param to The recipient's email address
     * @param subject The email subject
     * @param htmlContent The email body as HTML
     * @return true if the email was sent successfully, false otherwise
     */
    @Override
    public boolean sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("HTML email sent to: {}", to);
            return true;
        } catch (MessagingException e) {
            log.error("Failed to send HTML email to: {}. Error: {}", to, e.getMessage(), e);
            // Print stack trace to console for easier debugging
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Send a doctor invitation email with registration code.
     * 
     * @param to The doctor's email address
     * @param doctorName The doctor's name
     * @param invitationCode The invitation code
     * @param expirationDate The expiration date of the invitation code
     * @return true if the email was sent successfully, false otherwise
     */
    @Override
    public boolean sendDoctorInvitationEmail(String to, String doctorName, String invitationCode, String expirationDate) {
        try {
            // Create a simple HTML template for the email
            String registrationLink = frontendUrl + "/doctor/complete-registration?code=" + invitationCode;

            // Log the frontend URL and registration link for debugging
            log.info("Frontend URL: {}", frontendUrl);
            log.info("Registration link: {}", registrationLink);

            String htmlContent = 
                "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "    <meta charset=\"UTF-8\">" +
                "    <title>Complete Your HealEasy Doctor Registration</title>" +
                "    <style>" +
                "        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                "        .container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                "        .header { background-color: #4CAF50; color: white; padding: 10px; text-align: center; }" +
                "        .content { padding: 20px; }" +
                "        .button { display: inline-block; background-color: #4CAF50; color: white; padding: 10px 20px; " +
                "                 text-decoration: none; border-radius: 5px; margin-top: 20px; }" +
                "        .footer { font-size: 12px; color: #777; margin-top: 30px; }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class=\"container\">" +
                "        <div class=\"header\">" +
                "            <h1>HealEasy Doctor Registration</h1>" +
                "        </div>" +
                "        <div class=\"content\">" +
                "            <p>Dear " + doctorName + ",</p>" +
                "            <p>Congratulations! Your application to join HealEasy as a doctor has been approved.</p>" +
                "            <p>To complete your registration, please use the following invitation code:</p>" +
                "            <h2 style=\"text-align: center;\">" + invitationCode + "</h2>" +
                "            <p>This code will expire on " + expirationDate + ".</p>" +
                "            <p>Click the button below to complete your registration:</p>" +
                "            <div style=\"text-align: center;\">" +
                "                <a href=\"" + registrationLink + "\" class=\"button\">Complete Registration</a>" +
                "            </div>" +
                "            <p>If the button doesn't work, copy and paste the following URL into your browser:</p>" +
                "            <p>" + registrationLink + "</p>" +
                "        </div>" +
                "        <div class=\"footer\">" +
                "            <p>This is an automated message. Please do not reply to this email.</p>" +
                "            <p>&copy; " + java.time.Year.now().getValue() + " HealEasy. All rights reserved.</p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";

            // Send the email
            boolean result = sendHtmlEmail(to, "Complete Your HealEasy Doctor Registration", htmlContent);
            if (result) {
                log.info("Doctor invitation email sent successfully to: {}", to);
            } else {
                log.error("Failed to send doctor invitation email to: {}", to);
            }
            return result;
        } catch (Exception e) {
            log.error("Failed to send doctor invitation email to: {}. Error: {}", to, e.getMessage(), e);
            // Print stack trace to console for easier debugging
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Send a confirmation email to a doctor after they submit their application.
     * 
     * @param to The doctor's email address
     * @param doctorName The doctor's name
     * @return true if the email was sent successfully, false otherwise
     */
    @Override
    public boolean sendDoctorApplicationSubmissionEmail(String to, String doctorName) {
        try {
            // Create a simple HTML template for the email
            String htmlContent = 
                "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "    <meta charset=\"UTF-8\">" +
                "    <title>HealEasy Doctor Application Submitted</title>" +
                "    <style>" +
                "        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                "        .container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                "        .header { background-color: #4CAF50; color: white; padding: 10px; text-align: center; }" +
                "        .content { padding: 20px; }" +
                "        .footer { font-size: 12px; color: #777; margin-top: 30px; }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class=\"container\">" +
                "        <div class=\"header\">" +
                "            <h1>HealEasy Doctor Application</h1>" +
                "        </div>" +
                "        <div class=\"content\">" +
                "            <p>Dear " + doctorName + ",</p>" +
                "            <p>Thank you for submitting your application to join HealEasy as a doctor.</p>" +
                "            <p>Your application has been received and is currently under review by our administrative team.</p>" +
                "            <p>You will receive another email once your application has been reviewed with further instructions.</p>" +
                "            <p>If you have any questions, please contact our support team.</p>" +
                "        </div>" +
                "        <div class=\"footer\">" +
                "            <p>This is an automated message. Please do not reply to this email.</p>" +
                "            <p>&copy; " + java.time.Year.now().getValue() + " HealEasy. All rights reserved.</p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";

            // Send the email
            boolean result = sendHtmlEmail(to, "HealEasy Doctor Application Received", htmlContent);
            if (result) {
                log.info("Doctor application submission email sent successfully to: {}", to);
            } else {
                log.error("Failed to send doctor application submission email to: {}", to);
            }
            return result;
        } catch (Exception e) {
            log.error("Failed to send doctor application submission email to: {}. Error: {}", to, e.getMessage(), e);
            // Print stack trace to console for easier debugging
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Send a rejection email to a doctor when their application is rejected.
     * 
     * @param to The doctor's email address
     * @param doctorName The doctor's name
     * @param reason The reason for rejection (optional)
     * @return true if the email was sent successfully, false otherwise
     */
    @Override
    public boolean sendDoctorApplicationRejectionEmail(String to, String doctorName, String reason) {
        try {
            // Create a simple HTML template for the email
            String reasonHtml = reason != null && !reason.isEmpty() 
                ? "<p>Reason: " + reason + "</p>" 
                : "<p>If you have any questions about this decision, please contact our support team.</p>";

            String htmlContent = 
                "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "    <meta charset=\"UTF-8\">" +
                "    <title>HealEasy Doctor Application Status</title>" +
                "    <style>" +
                "        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                "        .container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                "        .header { background-color: #F44336; color: white; padding: 10px; text-align: center; }" +
                "        .content { padding: 20px; }" +
                "        .footer { font-size: 12px; color: #777; margin-top: 30px; }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class=\"container\">" +
                "        <div class=\"header\">" +
                "            <h1>HealEasy Doctor Application Status</h1>" +
                "        </div>" +
                "        <div class=\"content\">" +
                "            <p>Dear " + doctorName + ",</p>" +
                "            <p>Thank you for your interest in joining HealEasy as a doctor.</p>" +
                "            <p>After careful review of your application, we regret to inform you that we are unable to approve your application at this time.</p>" +
                reasonHtml +
                "            <p>You are welcome to apply again in the future with updated information.</p>" +
                "        </div>" +
                "        <div class=\"footer\">" +
                "            <p>This is an automated message. Please do not reply to this email.</p>" +
                "            <p>&copy; " + java.time.Year.now().getValue() + " HealEasy. All rights reserved.</p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";

            // Send the email
            boolean result = sendHtmlEmail(to, "HealEasy Doctor Application Status", htmlContent);
            if (result) {
                log.info("Doctor application rejection email sent successfully to: {}", to);
            } else {
                log.error("Failed to send doctor application rejection email to: {}", to);
            }
            return result;
        } catch (Exception e) {
            log.error("Failed to send doctor application rejection email to: {}. Error: {}", to, e.getMessage(), e);
            // Print stack trace to console for easier debugging
            e.printStackTrace();
            return false;
        }
    }
}
