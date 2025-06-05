package com.finalproject.backend.service;

import com.finalproject.backend.dto.pr.PrResponseDTO;
import org.springframework.http.*;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class PrInquiryService {
    private final RestTemplate restTemplate;

    public PrInquiryService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<PrResponseDTO> fetchPrs(String owner, String repo, OAuth2AccessToken token) {
        //호출할 URL
        String url = String.format("https://api.github.com/repos/%s/%s/pulls?state=all", owner, repo);

        //HTTP Header 세팅: Authorization: Bearer <토큰>
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.getTokenValue());
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<PrResponseDTO[]> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    PrResponseDTO[].class
            );

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                PrResponseDTO[] prArray = responseEntity.getBody();
                if (prArray != null) {
                    return Arrays.asList(prArray);
                }
            }

            return Collections.emptyList();

        } catch (HttpClientErrorException ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }
}
