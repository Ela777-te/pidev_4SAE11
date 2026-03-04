package com.example.review.service;

import com.example.review.client.NotificationClient;
import com.example.review.client.UserClient;
import com.example.review.dto.NotificationRequestDto;
import com.example.review.dto.UserDto;
import com.example.review.entity.Review;
import com.example.review.entity.ReviewResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Sends notifications when a review response (message) is received:
 * 1. Push notification via Notification microservice
 * 2. Email via Mailtrap (SMTP) to the reviewee
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewNotificationService {

    public static final String TYPE_REVIEW_RESPONSE = "REVIEW_RESPONSE";

    private final NotificationClient notificationClient;
    private final UserClient userClient;
    private final ReviewEmailService reviewEmailService;

    /**
     * Notify the reviewee when someone responds to a review about them.
     * Sends both push notification and email. Catches and logs any exception
     * so Review is not affected if Notification or User service is down.
     */
    public void notifyReviewResponseReceived(ReviewResponse response) {
        if (response == null || response.getReview() == null) {
            return;
        }
        Review review = response.getReview();
        Long revieweeId = review.getRevieweeId();
        if (revieweeId == null) {
            return;
        }
        String revieweeIdStr = String.valueOf(revieweeId);
        String messagePreview = truncate(response.getMessage(), 100);

        // 1. Push notification via Notification microservice
        sendPushNotification(revieweeIdStr, messagePreview, review, response);

        // 2. Email via Mailtrap (fetches user email from User microservice)
        sendEmailNotification(revieweeId, messagePreview, review, response);
    }

    private void sendPushNotification(String revieweeId, String messagePreview, Review review, ReviewResponse response) {
        String title = "New response to your review";
        String body = "Someone replied to a review about you: \"" + messagePreview + "\"";

        Map<String, String> data = new HashMap<>();
        data.put("reviewId", String.valueOf(review.getId()));
        data.put("reviewResponseId", String.valueOf(response.getId()));
        data.put("respondentId", String.valueOf(response.getRespondentId()));

        try {
            NotificationRequestDto request = NotificationRequestDto.builder()
                .userId(revieweeId)
                .title(title)
                .body(body)
                .type(TYPE_REVIEW_RESPONSE)
                .data(data)
                .build();
            notificationClient.create(request);
            log.debug("Sent push notification for review response {} to reviewee {}", response.getId(), revieweeId);
        } catch (Exception e) {
            log.warn("Failed to send push notification to reviewee {} for review response {}: {}",
                revieweeId, response.getId(), e.getMessage());
        }
    }

    private void sendEmailNotification(Long revieweeId, String messagePreview, Review review, ReviewResponse response) {
        try {
            UserDto user = userClient.getUserById(revieweeId);
            if (user == null || user.getEmail() == null || user.getEmail().isBlank()) {
                log.debug("No email for reviewee {} — skipping email notification", revieweeId);
                return;
            }
            String revieweeName = buildDisplayName(user.getFirstName(), user.getLastName(), revieweeId);
            reviewEmailService.sendReviewResponseEmail(
                user.getEmail(),
                revieweeName,
                messagePreview,
                review.getId(),
                response.getId()
            );
        } catch (Exception e) {
            log.warn("Failed to send email to reviewee {} for review response {}: {}",
                revieweeId, response.getId(), e.getMessage());
        }
    }

    private static String buildDisplayName(String firstName, String lastName, Long id) {
        if (firstName != null && lastName != null && !firstName.isBlank() && !lastName.isBlank()) {
            return (firstName + " " + lastName).trim();
        }
        if (firstName != null && !firstName.isBlank()) return firstName;
        if (lastName != null && !lastName.isBlank()) return lastName;
        return "User " + id;
    }

    private static String truncate(String s, int maxLen) {
        if (s == null) return "";
        return s.length() <= maxLen ? s : s.substring(0, maxLen) + "...";
    }
}
