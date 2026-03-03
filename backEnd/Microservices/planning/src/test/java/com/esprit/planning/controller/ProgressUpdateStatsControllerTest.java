package com.esprit.planning.controller;

import com.esprit.planning.dto.*;
import com.esprit.planning.service.ProgressUpdateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for ProgressUpdateStatsController. Verifies that stats and report endpoints
 * delegate to ProgressUpdateService and return the correct DTOs.
 */
@WebMvcTest(ProgressUpdateStatsController.class)
class ProgressUpdateStatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProgressUpdateService progressUpdateService;

    @Test
    void getStatsByFreelancer_returnsFreelancerStats() throws Exception {
        FreelancerProgressStatsDto dto = FreelancerProgressStatsDto.builder()
                .freelancerId(10L)
                .totalUpdates(5)
                .totalComments(3)
                .averageProgressPercentage(60.0)
                .lastUpdateAt(LocalDateTime.now())
                .updatesLast30Days(2)
                .build();
        when(progressUpdateService.getProgressStatisticsByFreelancer(10L)).thenReturn(dto);

        mockMvc.perform(get("/api/progress-updates/stats/freelancer/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.freelancerId").value(10))
                .andExpect(jsonPath("$.totalUpdates").value(5));
    }

    @Test
    void getStatsByProject_returnsProjectStats() throws Exception {
        ProjectProgressStatsDto dto = ProjectProgressStatsDto.builder()
                .projectId(1L)
                .updateCount(4)
                .commentCount(2)
                .currentProgressPercentage(75)
                .firstUpdateAt(LocalDateTime.now().minusDays(10))
                .lastUpdateAt(LocalDateTime.now())
                .build();
        when(progressUpdateService.getProgressStatisticsByProject(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/progress-updates/stats/project/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectId").value(1))
                .andExpect(jsonPath("$.updateCount").value(4));
    }

    @Test
    void getStatsByContract_returnsContractStats() throws Exception {
        ContractProgressStatsDto dto = ContractProgressStatsDto.builder()
                .contractId(1L)
                .updateCount(3)
                .commentCount(1)
                .currentProgressPercentage(50)
                .build();
        when(progressUpdateService.getProgressStatisticsByContract(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/progress-updates/stats/contract/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contractId").value(1));
    }

    @Test
    void getDashboardStats_returnsDashboardDto() throws Exception {
        DashboardStatsDto dto = DashboardStatsDto.builder()
                .totalUpdates(100)
                .totalComments(50)
                .averageProgressPercentage(55.5)
                .distinctProjectCount(20)
                .distinctFreelancerCount(15)
                .build();
        when(progressUpdateService.getDashboardStatistics()).thenReturn(dto);

        mockMvc.perform(get("/api/progress-updates/stats/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUpdates").value(100))
                .andExpect(jsonPath("$.distinctProjectCount").value(20));
    }

    @Test
    void getProjectReport_withFromTo_returnsReport() throws Exception {
        ProgressReportDto dto = ProgressReportDto.builder()
                .projectId(1L)
                .from(LocalDate.of(2025, 1, 1))
                .to(LocalDate.of(2025, 1, 31))
                .updateCount(5)
                .commentCount(2)
                .averageProgressPercentage(40.0)
                .firstUpdateAt(LocalDateTime.now())
                .lastUpdateAt(LocalDateTime.now())
                .build();
        when(progressUpdateService.getProgressReportForProject(eq(1L), any(), any())).thenReturn(dto);

        mockMvc.perform(get("/api/progress-updates/stats/report")
                        .param("projectId", "1")
                        .param("from", "2025-01-01")
                        .param("to", "2025-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectId").value(1))
                .andExpect(jsonPath("$.updateCount").value(5));
    }

    @Test
    void getProjectReport_withoutFromTo_callsServiceWithNulls() throws Exception {
        ProgressReportDto dto = ProgressReportDto.builder()
                .projectId(1L)
                .from(LocalDate.now().minusDays(30))
                .to(LocalDate.now())
                .updateCount(0)
                .commentCount(0)
                .build();
        when(progressUpdateService.getProgressReportForProject(eq(1L), isNull(), isNull())).thenReturn(dto);

        mockMvc.perform(get("/api/progress-updates/stats/report").param("projectId", "1"))
                .andExpect(status().isOk());
        verify(progressUpdateService).getProgressReportForProject(1L, null, null);
    }
}
