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
@Schema(description = "GitHub commit info (from GitHub API)")
public class GitHubCommitDto {

    @Schema(description = "Commit SHA")
    private String sha;

    @JsonProperty("html_url")
    @Schema(description = "URL to the commit on GitHub")
    private String htmlUrl;

    @JsonProperty("commit")
    @Schema(description = "Commit details")
    private CommitDetail commit;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CommitDetail {
        @Schema(description = "Commit message")
        private String message;

        @JsonProperty("author")
        @Schema(description = "Author info")
        private AuthorDetail author;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AuthorDetail {
        @Schema(description = "Author name")
        private String name;

        @Schema(description = "Commit date (ISO-8601)")
        private String date;
    }
}
