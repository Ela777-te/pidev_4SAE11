package com.esprit.planning.service;

import com.esprit.planning.dto.GitHubBranchDto;
import com.esprit.planning.dto.GitHubCommitDto;
import com.esprit.planning.dto.GitHubIssueRequest;
import com.esprit.planning.dto.GitHubIssueResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for GitHubApiService. Verifies isEnabled, getBranches, getCommits, getLatestCommit, createIssue
 * with mocked RestTemplate. When disabled or token empty, isEnabled returns false; API methods return empty/null.
 */
class GitHubApiServiceTest {

    @Test
    void isEnabled_whenTokenEmpty_returnsFalse() {
        GitHubApiService service = new GitHubApiService(mock(RestTemplate.class), "", false);
        assertThat(service.isEnabled()).isFalse();
    }

    @Test
    void isEnabled_whenDisabled_returnsFalse() {
        GitHubApiService service = new GitHubApiService(mock(RestTemplate.class), "token", false);
        assertThat(service.isEnabled()).isFalse();
    }

    @Test
    void isEnabled_whenEnabledAndTokenSet_returnsTrue() {
        GitHubApiService service = new GitHubApiService(mock(RestTemplate.class), "token", true);
        assertThat(service.isEnabled()).isTrue();
    }

    @Test
    void getBranches_whenDisabled_returnsEmptyList() {
        GitHubApiService service = new GitHubApiService(mock(RestTemplate.class), "", false);
        List<GitHubBranchDto> result = service.getBranches("owner", "repo");
        assertThat(result).isEmpty();
    }

    @Test
    void getLatestCommit_whenDisabled_returnsNull() {
        GitHubApiService service = new GitHubApiService(mock(RestTemplate.class), "", false);
        GitHubCommitDto result = service.getLatestCommit("o", "r", null);
        assertThat(result).isNull();
    }

    @Test
    void getCommits_whenDisabled_returnsEmptyList() {
        GitHubApiService service = new GitHubApiService(mock(RestTemplate.class), "", false);
        List<GitHubCommitDto> result = service.getCommits("o", "r", null, 30);
        assertThat(result).isEmpty();
    }

    @Test
    void createIssue_whenDisabled_returnsNull() {
        GitHubApiService service = new GitHubApiService(mock(RestTemplate.class), "", false);
        GitHubIssueResponseDto result = service.createIssue("o", "r", new GitHubIssueRequest("Title", "Body"));
        assertThat(result).isNull();
    }

    @Test
    void createIssue_whenTitleBlank_returnsNull() {
        GitHubApiService service = new GitHubApiService(mock(RestTemplate.class), "token", true);
        GitHubIssueResponseDto result = service.createIssue("o", "r", new GitHubIssueRequest("  ", "Body"));
        assertThat(result).isNull();
    }
}
