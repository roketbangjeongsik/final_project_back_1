package com.finalproject.backend.service;

import com.finalproject.backend.dto.GithubRepoResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class GithubService {

    private final RestTemplate restTemplate;
    private static final String GITHUB_API_URL = "https://api.github.com";

    public GithubService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private HttpHeaders createHeaders(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("유효한 GitHub 토큰이 필요합니다.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.valueOf("application/vnd.github+json")));
        headers.setBearerAuth(token);
        headers.set("X-GitHub-Api-Version", "2022-11-28");
        return headers;
    }

    @Cacheable(value = "githubRepoCache", key = "#owner + '/' + #repo")
    public GithubRepoResponse getRepositoryInfo(String owner, String repo, String token) {
        // 입력값 정리 및 유효성 검증
        owner = sanitize(owner, "owner");
        repo = sanitize(repo, "repo");

        HttpHeaders headers = createHeaders(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = GITHUB_API_URL + "/repos/" + owner + "/" + repo;

        try {
            ResponseEntity<GithubRepoResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    GithubRepoResponse.class
            );
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("GitHub API 요청 실패 (Client Error): " + e.getStatusCode(), e);
        } catch (ResourceAccessException e) {
            throw new RuntimeException("GitHub API에 접근할 수 없습니다 (네트워크 오류): " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("GitHub 저장소 정보를 가져오는 데 실패했습니다: " + (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()), e);
        }
    }

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
