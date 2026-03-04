package com.esprit.planning.controller;

import com.esprit.planning.repository.ProgressUpdateRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for PlanningHealthController. Verifies health endpoint returns 200 with database UP
 * when repository succeeds, and 503 with DEGRADED status when repository throws.
 */
@WebMvcTest(PlanningHealthController.class)
class PlanningHealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProgressUpdateRepository progressUpdateRepository;

    @Test
    void health_whenDbOk_returns200WithStatusUp() throws Exception {
        when(progressUpdateRepository.count()).thenReturn(42L);

        mockMvc.perform(get("/api/planning/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.service").value("planning"))
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.database.status").value("UP"))
                .andExpect(jsonPath("$.database.progressUpdateCount").value(42));
    }

    @Test
    void health_whenDbThrows_returns503WithDegraded() throws Exception {
        when(progressUpdateRepository.count()).thenThrow(new RuntimeException("Connection refused"));

        mockMvc.perform(get("/api/planning/health"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value("DEGRADED"))
                .andExpect(jsonPath("$.database.status").value("DOWN"))
                .andExpect(jsonPath("$.database.error").value(org.hamcrest.Matchers.containsString("Connection refused")));
    }
}
