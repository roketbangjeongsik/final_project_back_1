package com.finalproject.backend.controller;
//DTO
//Service
import com.finalproject.backend.dto.github.*;
import com.finalproject.backend.service.GithubService;
//Spring
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.*;
//swagger
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
//Utils
import java.util.List;

@RestController
@RequestMapping("/api/v1/github")
@RequiredArgsConstructor
public class GithubController {

    private final GithubService githubService;

    // 특정 레포지토리 정보 조회
    @Operation(summary = "사용자 레포지토리 선택 조회")
    @GetMapping("/repos/{owner}/{repo}")
    public ResponseEntity<GithubRepoResponseDTO> getRepositoryInfo(
            @PathVariable String owner,
            @PathVariable String repo,
            @Parameter(hidden = true)
            @RegisteredOAuth2AuthorizedClient("github") OAuth2AuthorizedClient client) {

        String token = getToken(client);

        GithubRepoResponseDTO response = githubService.getRepositoryInfo(
                sanitize(owner, "owner"),
                sanitize(repo, "repo"),
                token
        );

        return ResponseEntity.ok(response);
    }

    // 사용자 레포지토리 리스트 조회
    @Operation(summary = "사용자 레포지토리 목록 조회")
    @GetMapping("/users/{username}/repos")
    public ResponseEntity<List<GithubRepoListItemDTO>> getUserRepositories(
            @PathVariable String username,
            @Parameter(hidden = true)
            @RegisteredOAuth2AuthorizedClient("github") OAuth2AuthorizedClient client) {

        String token = getToken(client);

        List<GithubRepoListItemDTO> repositories = githubService.getUserRepositories(
                sanitize(username, "username"),
                token
        );

        return ResponseEntity.ok(repositories);
    }

    //Pull Request 커밋 목록 조회
    @Operation(summary = "Pull Request 커밋 목록 조회")
    @GetMapping("/repos/{owner}/{repo}/pulls/{pullNumber}/commits")
    public ResponseEntity<List<GithubPullRequestCommitDTO>> getPullRequestCommits(
            @PathVariable String owner,
            @PathVariable String repo,
            @PathVariable int pullNumber,
            @Parameter(hidden = true)
            @RegisteredOAuth2AuthorizedClient("github") OAuth2AuthorizedClient client) {

        String token = getToken(client);

        List<GithubPullRequestCommitDTO> commits = githubService.getPullRequestCommits(
                sanitize(owner, "owner"),
                sanitize(repo, "repo"),
                pullNumber,
                token
        );

        return ResponseEntity.ok(commits);
    }

    // 커밋 상세 정보 조회
    @Operation(summary = "커밋 상세 정보 조회", description = "특정 커밋(ref)의 상세 정보를 조회합니다.")
    @GetMapping("/repos/{owner}/{repo}/commits/{ref}")
    public ResponseEntity<GithubCommitDetailDTO> getCommitDetails(
            @PathVariable String owner,
            @PathVariable String repo,
            @PathVariable String ref,
            @Parameter(hidden = true)
            @RegisteredOAuth2AuthorizedClient("github") OAuth2AuthorizedClient client) {

        String token = getToken(client);

        GithubCommitDetailDTO commit = githubService.getCommitDetails(
                sanitize(owner, "owner"),
                sanitize(repo, "repo"),
                sanitize(ref, "ref"),
                token
        );

        return ResponseEntity.ok(commit);
    }

    // 두 커밋 간의 diff 조회
    @Operation(summary = " 두 커밋 간의 diff 조회", description = "엔드포인트를 사용하여 두 커밋 간의 차이 비교")
    @GetMapping("/repos/{owner}/{repo}/compare/{basehead}")
    public ResponseEntity<GithubCompareResponseDTO> compareCommits(
            @PathVariable String owner,
            @PathVariable String repo,
            @PathVariable String basehead,
            @Parameter(hidden = true)
            @RegisteredOAuth2AuthorizedClient("github") OAuth2AuthorizedClient client) {

        String token = getToken(client);

        GithubCompareResponseDTO response = githubService.compareCommits(
                sanitize(owner, "owner"),
                sanitize(repo, "repo"),
                sanitize(basehead, "basehead"),
                token
        );

        return ResponseEntity.ok(response);
    }


    // 액세스 토큰 추출
    private String getToken(OAuth2AuthorizedClient client) {
        if (client == null || client.getAccessToken() == null) {
            throw new IllegalStateException("GitHub 인증 토큰을 가져올 수 없습니다.");
        }
        return client.getAccessToken().getTokenValue();
    }

    // 입력값 정리 및 검증
    private String sanitize(String input, String fieldName) {
        if (input == null) {
            throw new IllegalArgumentException(fieldName + "는 null일 수 없습니다.");
        }
        String cleaned = input.trim().replaceAll("\\p{Cntrl}", "");
        if (!cleaned.matches("^[a-zA-Z0-9._-]+$")) {
            throw new IllegalArgumentException(fieldName + " 형식이 잘못되었습니다: " + cleaned);
        }
        return cleaned;
    }
}

