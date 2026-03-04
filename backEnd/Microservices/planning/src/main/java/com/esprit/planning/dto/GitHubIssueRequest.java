package com.esprit.planning.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a GitHub issue")
public class GitHubIssueRequest {

    @Schema(description = "Issue title", requiredMode = Schema.RequiredMode.REQUIRED, example = "Stalled project: Project X")
    private String title;

    @Schema(description = "Issue body (markdown supported)", example = "No progress update in 7 days for project X.")
    private String body;
}
