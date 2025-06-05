package com.finalproject.backend.controller;

import com.finalproject.backend.dto.GithubRepoResponse;
import com.finalproject.backend.exception.UnauthorizedException;
import com.finalproject.backend.service.GithubService;
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
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {

        // 토큰 추출
        String token = extractToken(authorizationHeader);

        // 제어문자 제거 및 유효성 검사
        String cleanedOwner = sanitize(owner);
        String cleanedRepo = sanitize(repo);

        GithubRepoResponse response = githubService.getRepositoryInfo(cleanedOwner, cleanedRepo, token);
        return ResponseEntity.ok(response);
    }

    // Authorization 헤더에서 Bearer 토큰 추출
    private String extractToken(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            throw new UnauthorizedException("Authorization 헤더가 없거나 형식이 잘못되었습니다.");
        }
        return header.substring(7);
    }

    // 제어문자 제거 및 간단한 유효성 검사
    private String sanitize(String input) {
        if (input == null) return null;

        // 제어문자 제거
        String cleaned = input.trim().replaceAll("\\p{Cntrl}", "");

        // 간단한 유효성 검사 (원한다면 더 강하게)
        if (!cleaned.matches("^[a-zA-Z0-9._-]+$")) {
            throw new IllegalArgumentException("owner 또는 repo 이름이 유효하지 않습니다: " + cleaned);
        }

        return cleaned;
    }
}
