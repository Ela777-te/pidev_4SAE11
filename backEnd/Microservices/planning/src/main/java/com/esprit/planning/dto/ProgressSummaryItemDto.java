package com.esprit.planning.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Lightweight summary item for bulk progress update queries.
 * Used by GET /summary (projectIds or contractIds) and GET /freelancer/{id}/projects-summary.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Lightweight progress summary for a project or contract")
public class ProgressSummaryItemDto {

    @Schema(description = "Project ID (present for project or freelancer summary)")
    private Long projectId;

    @Schema(description = "Contract ID (present for contract summary)")
    private Long contractId;

    @Schema(description = "Current progress percentage (0-100)", example = "75")
    private Integer currentProgressPercentage;

    @Schema(description = "Timestamp of the most recent progress update")
    private LocalDateTime lastUpdateAt;
}
