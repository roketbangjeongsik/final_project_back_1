package com.finalproject.backend.service;

import com.finalproject.backend.dto.research.DocumentDTO;
import com.finalproject.backend.dto.research.SearchResponseDTO;
import com.finalproject.backend.entity.ResearchDocument;
import com.finalproject.backend.repository.ResearchDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResearchService {

    private final RestTemplate restTemplate;
    private final ResearchDocumentRepository documentRepository;

    // FastAPI 서버 URL
    @Value("${fastapi.server.url}")
    private String fastApiServerUrl;

    // FastAPI 호출: 쿼리 전달 → id 리스트 반환 (예: [1,2,3,...])
    public List<Integer> searchViaPythonServer(String query) {
        String url = fastApiServerUrl + "/pipeline/search";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 바디로 쿼리 전달 (JSON 형식)
        String requestBody = "{\"query\": \"" + query + "\"}";

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<SearchResponseDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    SearchResponseDTO.class
            );

            SearchResponseDTO searchResponse = response.getBody();
            return searchResponse != null ? searchResponse.getIds() : List.of();
        } catch (Exception e) {
            // 에러 발생 시 로깅하고 빈 리스트 반환
            System.err.println("FastAPI 서버 호출 중 에러 발생: " + e.getMessage());
            return List.of();
        }
    }

    // DB에서 id 리스트로 문서 조회 후 DTO 변환
    public List<DocumentDTO> findDocumentsByIds(List<Integer> ids) {
        if (ids.isEmpty()) {
            return List.of();  // 빈 리스트면 바로 반환
        }

        List<ResearchDocument> documents = documentRepository.findAllById(ids);

        // 문서 id 순서 유지 위해 정렬
        documents.sort(Comparator.comparingInt(d -> ids.indexOf(d.getId())));

        return documents.stream()
                .map(doc -> new DocumentDTO(doc.getId(), doc.getTitle(), doc.getContents(), doc.getUrl(), doc.getDatetime()))
                .collect(Collectors.toList());
    }
}
