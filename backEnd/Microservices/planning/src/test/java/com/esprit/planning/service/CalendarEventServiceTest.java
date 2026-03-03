package com.esprit.planning.service;

import com.esprit.planning.client.ProjectClient;
import com.esprit.planning.dto.CalendarEventDto;
import com.esprit.planning.dto.ProjectDto;
import com.esprit.planning.entity.ProgressUpdate;
import com.esprit.planning.repository.ProgressUpdateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Unit tests for CalendarEventService. Verifies listEventsFromDb with and without userId/role;
 * mocks ProgressUpdateRepository and ProjectClient.
 */
@ExtendWith(MockitoExtension.class)
class CalendarEventServiceTest {

    @Mock
    private ProgressUpdateRepository progressUpdateRepository;

    @Mock
    private ProjectClient projectClient;

    @InjectMocks
    private CalendarEventService calendarEventService;

    @Test
    void listEventsFromDb_withoutUserId_returnsEventsFromUpdatesAndProjects() {
        ProgressUpdate pu = new ProgressUpdate();
        pu.setId(1L);
        pu.setProjectId(1L);
        pu.setNextUpdateDue(LocalDateTime.now().plusDays(1));
        pu.setTitle("Update");
        when(progressUpdateRepository.findByNextUpdateDueBetween(any(), any())).thenReturn(List.of(pu));
        when(projectClient.getProjects()).thenReturn(List.of());

        List<CalendarEventDto> result = calendarEventService.listEventsFromDb(
                LocalDateTime.now(), LocalDateTime.now().plusMonths(1));

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getSummary()).contains("Next progress update due");
        assertThat(result.get(0).getId()).isEqualTo("pu-1");
    }

    @Test
    void listEventsFromDb_withUserIdAndRole_callsRepositoryAndFilters() {
        when(progressUpdateRepository.findByNextUpdateDueBetween(any(), any())).thenReturn(List.of());
        when(projectClient.getProjects()).thenReturn(List.of());
        when(progressUpdateRepository.findDistinctProjectIdsByFreelancerId(5L)).thenReturn(List.of(1L));

        List<CalendarEventDto> result = calendarEventService.listEventsFromDb(
                LocalDateTime.now(), LocalDateTime.now().plusMonths(1), 5L, "FREELANCER");

        assertThat(result).isNotNull();
    }
}
