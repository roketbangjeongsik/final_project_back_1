package com.finalproject.backend.controller;

import com.finalproject.backend.dto.GithubRepoResponse;
import com.finalproject.backend.exception.UnauthorizedException;
import com.finalproject.backend.service.GithubService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/github")
@RequiredArgsConstructor
public class GithubController {

    private final GithubService githubService;

    @GetMapping("/repos/{owner}/{repo}")
    public ResponseEntity<GithubRepoResponse> getRepositoryInfo(
            @PathVariable String owner,
            @PathVariable String repo,
            @RegisteredOAuth2AuthorizedClient("github") OAuth2AuthorizedClient client) {

        // 토큰은 client에서 꺼냄
        String token = client.getAccessToken().getTokenValue();

        String cleanedOwner = sanitize(owner);
        String cleanedRepo = sanitize(repo);

        GithubRepoResponse response = githubService.getRepositoryInfo(cleanedOwner, cleanedRepo, token);
        return ResponseEntity.ok(response);
    }

    private String sanitize(String input) {
        if (input == null) return null;

        String cleaned = input.trim().replaceAll("\\p{Cntrl}", "");

        if (!cleaned.matches("^[a-zA-Z0-9._-]+$")) {
            throw new IllegalArgumentException("owner 또는 repo 이름이 유효하지 않습니다: " + cleaned);
        }

        return cleaned;
    }
}

