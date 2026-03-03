package com.esprit.planning.controller;

import com.esprit.planning.dto.GitHubBranchDto;
import com.esprit.planning.dto.GitHubCommitDto;
import com.esprit.planning.dto.GitHubIssueRequest;
import com.esprit.planning.dto.GitHubIssueResponseDto;
import com.esprit.planning.service.GitHubApiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for GitHubController. Verifies branches, commits, latest commit, create issue, and enabled endpoints;
 * when GitHub is disabled returns 503 for protected endpoints.
 */
@WebMvcTest(GitHubController.class)
class GitHubControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GitHubApiService githubApiService;

    @Test
    void isEnabled_returnsTrueWhenEnabled() throws Exception {
        when(githubApiService.isEnabled()).thenReturn(true);

        mockMvc.perform(get("/api/github/enabled"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void isEnabled_returnsFalseWhenDisabled() throws Exception {
        when(githubApiService.isEnabled()).thenReturn(false);

        mockMvc.perform(get("/api/github/enabled"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void getBranches_whenEnabled_returns200AndList() throws Exception {
        when(githubApiService.isEnabled()).thenReturn(true);
        when(githubApiService.getBranches("owner", "repo"))
                .thenReturn(List.of(new GitHubBranchDto("main", null)));

        mockMvc.perform(get("/api/github/repos/owner/repo/branches"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("main"));
        verify(githubApiService).getBranches("owner", "repo");
    }

    @Test
    void getBranches_whenDisabled_returns503() throws Exception {
        when(githubApiService.isEnabled()).thenReturn(false);

        mockMvc.perform(get("/api/github/repos/owner/repo/branches"))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    void getCommits_whenEnabled_returns200() throws Exception {
        when(githubApiService.isEnabled()).thenReturn(true);
        when(githubApiService.getCommits(eq("o"), eq("r"), any(), eq(30)))
                .thenReturn(List.of(new GitHubCommitDto()));

        mockMvc.perform(get("/api/github/repos/o/r/commits").param("perPage", "30"))
                .andExpect(status().isOk());
        verify(githubApiService).getCommits("o", "r", null, 30);
    }

    @Test
    void getCommits_whenDisabled_returns503() throws Exception {
        when(githubApiService.isEnabled()).thenReturn(false);

        mockMvc.perform(get("/api/github/repos/o/r/commits"))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    void getLatestCommit_whenEnabledAndFound_returns200() throws Exception {
        GitHubCommitDto commit = new GitHubCommitDto();
        commit.setSha("abc123");
        when(githubApiService.isEnabled()).thenReturn(true);
        when(githubApiService.getLatestCommit("o", "r", null)).thenReturn(commit);

        mockMvc.perform(get("/api/github/repos/o/r/commits/latest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sha").value("abc123"));
    }

    @Test
    void getLatestCommit_whenEnabledAndNotFound_returns404() throws Exception {
        when(githubApiService.isEnabled()).thenReturn(true);
        when(githubApiService.getLatestCommit("o", "r", null)).thenReturn(null);

        mockMvc.perform(get("/api/github/repos/o/r/commits/latest"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getLatestCommit_whenDisabled_returns503() throws Exception {
        when(githubApiService.isEnabled()).thenReturn(false);

        mockMvc.perform(get("/api/github/repos/o/r/commits/latest"))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    void createIssue_whenEnabledAndValid_returns201() throws Exception {
        GitHubIssueResponseDto created = new GitHubIssueResponseDto();
        created.setNumber(1);
        created.setHtmlUrl("https://github.com/o/r/issues/1");
        created.setTitle("Bug");
        created.setState("open");
        when(githubApiService.isEnabled()).thenReturn(true);
        when(githubApiService.createIssue(eq("o"), eq("r"), any())).thenReturn(created);

        mockMvc.perform(post("/api/github/repos/o/r/issues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Bug\",\"body\":\"Description\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.title").value("Bug"));
    }

    @Test
    void createIssue_whenTitleBlank_returns400() throws Exception {
        when(githubApiService.isEnabled()).thenReturn(true);

        mockMvc.perform(post("/api/github/repos/o/r/issues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"  \",\"body\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createIssue_whenDisabled_returns503() throws Exception {
        when(githubApiService.isEnabled()).thenReturn(false);

        mockMvc.perform(post("/api/github/repos/o/r/issues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Bug\",\"body\":\"\"}"))
                .andExpect(status().isServiceUnavailable());
    }
}
