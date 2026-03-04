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
@Schema(description = "GitHub branch info (from GitHub API)")
public class GitHubBranchDto {

    @Schema(description = "Branch name")
    private String name;

    @JsonProperty("commit")
    @Schema(description = "Latest commit SHA on this branch")
    private CommitRef commitRef;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CommitRef {
        @JsonProperty("sha")
        private String sha;
    }
}
