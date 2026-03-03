package com.esprit.planning.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "GitHub issue creation response")
public class GitHubIssueResponseDto {

    @Schema(description = "Issue number")
    private Integer number;

    @JsonProperty("html_url")
    @Schema(description = "URL to the issue on GitHub")
    private String htmlUrl;

    @Schema(description = "Issue title")
    private String title;

    @Schema(description = "Issue state")
    private String state;
}
