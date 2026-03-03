package com.esprit.planning.controller;

import com.esprit.planning.dto.CalendarEventDto;
import com.esprit.planning.service.CalendarEventService;
import com.esprit.planning.service.GoogleCalendarService;
import com.esprit.planning.service.ProgressUpdateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for CalendarController. Verifies sync-project-deadline and list events endpoints;
 * mocks Google Calendar and CalendarEventService so no real external calls are made.
 */
@WebMvcTest(CalendarController.class)
class CalendarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GoogleCalendarService googleCalendarService;

    @MockitoBean
    private ProgressUpdateService progressUpdateService;

    @MockitoBean
    private CalendarEventService calendarEventService;

    @Test
    void syncProjectDeadline_returns200() throws Exception {
        mockMvc.perform(post("/api/calendar/sync-project-deadline")
                        .param("projectId", "1")
                        .param("freelancerId", "10"))
                .andExpect(status().isOk());
        verify(progressUpdateService).ensureProjectDeadlineInCalendarForUser(1L, 10L);
    }

    @Test
    void listEvents_withUserId_callsListEventsFromDbScopedToUser() throws Exception {
        CalendarEventDto dto = CalendarEventDto.builder()
                .id("pu-1")
                .summary("Next progress update due")
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .description("Update #1")
                .build();
        when(calendarEventService.listEventsFromDb(any(), any(), eq(5L), eq("FREELANCER")))
                .thenReturn(List.of(dto));
        when(googleCalendarService.isAvailable()).thenReturn(false);

        mockMvc.perform(get("/api/calendar/events")
                        .param("userId", "5")
                        .param("role", "FREELANCER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("pu-1"))
                .andExpect(jsonPath("$[0].summary").value("Next progress update due"));
        verify(calendarEventService).listEventsFromDb(any(), any(), eq(5L), eq("FREELANCER"));
    }

    @Test
    void listEvents_withoutUserId_whenGoogleAvailable_usesGoogleCalendar() throws Exception {
        when(googleCalendarService.isAvailable()).thenReturn(true);
        when(googleCalendarService.listEvents(any(), any(), any())).thenReturn(List.of());

        mockMvc.perform(get("/api/calendar/events"))
                .andExpect(status().isOk());
        verify(googleCalendarService).listEvents(any(), any(), any());
    }

    @Test
    void listEvents_withoutUserId_whenGoogleUnavailable_usesDb() throws Exception {
        when(googleCalendarService.isAvailable()).thenReturn(false);
        when(calendarEventService.listEventsFromDb(any(), any())).thenReturn(List.of());

        mockMvc.perform(get("/api/calendar/events"))
                .andExpect(status().isOk());
        verify(calendarEventService).listEventsFromDb(any(), any());
    }
}
