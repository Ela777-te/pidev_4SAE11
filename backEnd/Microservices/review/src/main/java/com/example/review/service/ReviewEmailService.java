package com.example.review.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Sends email notifications via Mailtrap (or any SMTP provider).
 * Used when a user receives a message on a review (review response).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewEmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:noreply@smart-freelance.local}")
    private String fromEmail;

    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

    /**
     * Sends an email to the reviewee when someone responds to a review about them.
     * Runs asynchronously to avoid blocking the HTTP response.
     */
    @Async
    public void sendReviewResponseEmail(String toEmail, String revieweeName,
                                        String messagePreview, Long reviewId, Long reviewResponseId) {
        if (!mailEnabled) {
            log.debug("[EMAIL] Mail disabled (app.mail.enabled=false) — skipping review response email to {}", toEmail);
            return;
        }
        if (toEmail == null || toEmail.isBlank()) {
            log.warn("[EMAIL] No email address for reviewee — skipping review response {}", reviewResponseId);
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("New response to your review — Smart Freelance");

            String displayName = revieweeName != null && !revieweeName.isBlank() ? revieweeName : "there";

            String html = """
                    <!DOCTYPE html>
                    <html>
                    <head><meta charset="UTF-8"></head>
                    <body style="font-family: Arial, sans-serif; background: #f4f4f4; margin: 0; padding: 20px;">
                      <div style="max-width: 600px; margin: auto; background: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.1);">
                        <div style="background: linear-gradient(135deg, #E37E33, #E23D59); padding: 30px 40px;">
                          <h1 style="color: #ffffff; margin: 0; font-size: 22px;">New response to your review</h1>
                          <p style="color: rgba(255,255,255,0.9); margin: 8px 0 0;">Someone replied to a review about you</p>
                        </div>
                        <div style="padding: 32px 40px;">
                          <p style="color: #374151; font-size: 16px;">Hello <strong>%s</strong>,</p>
                          <p style="color: #374151;">You have received a new message on a review about you:</p>
                          <div style="background: #f9fafb; border-left: 4px solid #E37E33; padding: 16px 20px; border-radius: 0 8px 8px 0; margin: 20px 0;">
                            <p style="margin: 0; color: #1f2937; font-style: italic;">"%s"</p>
                          </div>
                          <p style="color: #6b7280; font-size: 14px;">Log in to your dashboard to read the full message and respond.</p>
                          <div style="text-align: center; margin: 28px 0;">
                            <a href="http://localhost:4200/dashboard/reviews"
                               style="background: linear-gradient(135deg, #E37E33, #E23D59); color: #ffffff; text-decoration: none;
                                      padding: 14px 32px; border-radius: 8px; font-weight: bold; font-size: 15px; display: inline-block;">
                              View reviews
                            </a>
                          </div>
                        </div>
                        <div style="background: #f9fafb; padding: 20px 40px; text-align: center; border-top: 1px solid #e5e7eb;">
                          <p style="color: #9ca3af; font-size: 12px; margin: 0;">Smart Freelance — Review notification</p>
                        </div>
                      </div>
                    </body>
                    </html>
                    """.formatted(displayName, messagePreview != null ? messagePreview : "");

            helper.setText(html, true);
            mailSender.send(message);
            log.info("[EMAIL] Review response notification sent to {} for review {} / response {}", toEmail, reviewId, reviewResponseId);
        } catch (Exception e) {
            log.error("[EMAIL] Failed to send review response email to {}: {}", toEmail, e.getMessage());
        }
    }
}
