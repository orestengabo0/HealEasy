package org.healeasy.Iservices;

/**
 * Service interface for email operations.
 */
public interface IEmailService {

    /**
     * Send a simple text email.
     * 
     * @param to The recipient's email address
     * @param subject The email subject
     * @param text The email body text
     * @return true if the email was sent successfully, false otherwise
     */
    boolean sendSimpleEmail(String to, String subject, String text);

    /**
     * Send an HTML email.
     * 
     * @param to The recipient's email address
     * @param subject The email subject
     * @param htmlContent The email body as HTML
     * @return true if the email was sent successfully, false otherwise
     */
    boolean sendHtmlEmail(String to, String subject, String htmlContent);

    /**
     * Send a doctor invitation email with registration code.
     * 
     * @param to The doctor's email address
     * @param doctorName The doctor's name
     * @param invitationCode The invitation code
     * @param expirationDate The expiration date of the invitation code
     * @return true if the email was sent successfully, false otherwise
     */
    boolean sendDoctorInvitationEmail(String to, String doctorName, String invitationCode, String expirationDate);

    /**
     * Send a confirmation email to a doctor after they submit their application.
     * 
     * @param to The doctor's email address
     * @param doctorName The doctor's name
     * @return true if the email was sent successfully, false otherwise
     */
    boolean sendDoctorApplicationSubmissionEmail(String to, String doctorName);

    /**
     * Send a rejection email to a doctor when their application is rejected.
     * 
     * @param to The doctor's email address
     * @param doctorName The doctor's name
     * @param reason The reason for rejection (optional)
     * @return true if the email was sent successfully, false otherwise
     */
    boolean sendDoctorApplicationRejectionEmail(String to, String doctorName, String reason);
}
