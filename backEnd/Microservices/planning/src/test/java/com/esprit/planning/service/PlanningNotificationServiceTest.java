package com.esprit.planning.service;

import com.esprit.planning.client.NotificationClient;
import com.esprit.planning.dto.NotificationRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for PlanningNotificationService. Verifies notifyUser builds the correct
 * NotificationRequestDto and calls the NotificationClient.
 */
@ExtendWith(MockitoExtension.class)
class PlanningNotificationServiceTest {

    @Mock
    private NotificationClient notificationClient;

    @InjectMocks
    private PlanningNotificationService planningNotificationService;

    @Test
    void notifyUser_callsClientWithCorrectPayload() {
        ArgumentCaptor<NotificationRequestDto> captor = ArgumentCaptor.forClass(NotificationRequestDto.class);

        planningNotificationService.notifyUser("5", "Test title", "Test body",
                PlanningNotificationService.TYPE_PROGRESS_UPDATE,
                Map.of("progressUpdateId", "1", "projectId", "2"));

        verify(notificationClient).create(captor.capture());
        NotificationRequestDto dto = captor.getValue();
        assertThat(dto.getUserId()).isEqualTo("5");
        assertThat(dto.getTitle()).isEqualTo("Test title");
        assertThat(dto.getBody()).isEqualTo("Test body");
        assertThat(dto.getType()).isEqualTo(PlanningNotificationService.TYPE_PROGRESS_UPDATE);
        assertThat(dto.getData()).containsEntry("progressUpdateId", "1").containsEntry("projectId", "2");
    }

    @Test
    void notifyUser_whenUserIdNull_doesNotCallClient() {
        planningNotificationService.notifyUser(null, "Title", "Body", "TYPE", Map.of());
        verify(notificationClient, never()).create(org.mockito.ArgumentMatchers.any());
    }
}
