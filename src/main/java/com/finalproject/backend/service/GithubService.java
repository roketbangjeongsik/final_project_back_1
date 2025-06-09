package com.finalproject.backend.service;

//DTO

//Spring
import com.finalproject.backend.dto.github.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
//Utils
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GithubService {

    private final RestTemplate restTemplate;
    private static final String GITHUB_API_URL = "https://api.github.com";

// 사용자의 저장소 리스트 조회
    public List<GithubRepoListItemDTO> getUserRepositories(String token) {
        String url = String.format("%s/user/repos", GITHUB_API_URL);
        GithubRepoListItemDTO[] repoArray = fetchGithubData(url, token, GithubRepoListItemDTO[].class);
        return Arrays.asList(repoArray);
    }


// 개별 저장소 정보 조회 (캐시 사용)
    @Cacheable(value = "githubRepoCache", key = "#owner + '/' + #repo")
    public GithubRepoResponseDTO getRepositoryInfo(String owner, String repo, String token) {
        String url = String.format("%s/repos/%s/%s", GITHUB_API_URL,
                sanitize(owner, "owner"),
                sanitize(repo, "repo"));
        return fetchGithubData(url, token, GithubRepoResponseDTO.class);
    }
// PR 내 커밋 목록 조회
    public List<GithubPullRequestCommitDTO> getPullRequestCommits(String owner, String repo, int pullNumber, String token) {
        String url = GITHUB_API_URL + "/repos/" + owner + "/" + repo + "/pulls/" + pullNumber + "/commits";

        HttpHeaders headers = createHeaders(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<GithubPullRequestCommitDTO[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                GithubPullRequestCommitDTO[].class
        );

        return Arrays.asList(response.getBody());
    }

    public GithubCompareResponseDTO compareCommits(String owner, String repo, String basehead, String token) {
        String url = GITHUB_API_URL + "/repos/" + owner + "/" + repo + "/compare/" + basehead;

        HttpHeaders headers = createHeaders(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<GithubCompareResponseDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    GithubCompareResponseDTO.class
            );
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("GitHub API 요청 실패 (Client Error): " + e.getStatusCode(), e);
        } catch (ResourceAccessException e) {
            throw new RuntimeException("GitHub API에 접근할 수 없습니다 (네트워크 오류): " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("두 커밋 비교에 실패했습니다: " + e.getMessage(), e);
        }
    }

//    GitHub API 요청 공통 처리
    private <T> T fetchGithubData(String url, String token, Class<T> responseType) {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders(token));

        try {
            ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.GET, entity, responseType);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("GitHub API 요청 실패 (Client Error): " + e.getStatusCode(), e);
        } catch (ResourceAccessException e) {
            throw new RuntimeException("GitHub API에 접근할 수 없습니다 (네트워크 오류): " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("GitHub 데이터 가져오기 실패: " + (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()), e);
        }
    }

    public GithubCommitDetailDTO getCommitDetails(String owner, String repo, String ref, String token) {
        String url = GITHUB_API_URL + "/repos/" + owner + "/" + repo + "/commits/" + ref;

        HttpHeaders headers = createHeaders(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<GithubCommitDetailDTO> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                GithubCommitDetailDTO.class
        );

        return response.getBody();
    }

// 헤더 생성
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

// 입력값 유효성 검증
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
