package com.finalproject.backend.controller;

import com.finalproject.backend.dto.pr.PrResponseDTO;
import com.finalproject.backend.service.PrInquiryService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/v1")
@RestController
public class PrInquiryController {
    private final PrInquiryService prInquiryService;

    public PrInquiryController(PrInquiryService prInquiryService) {
        this.prInquiryService = prInquiryService;
    }

    @GetMapping("/user/prs/{owner}/{repo}")
    public List<PrResponseDTO> getPrsByRepo(
            @AuthenticationPrincipal OAuth2User principal,
            @RegisteredOAuth2AuthorizedClient("github") OAuth2AuthorizedClient client,
            @PathVariable String owner,
            @PathVariable String repo
    ) {
        OAuth2AccessToken token = client.getAccessToken();

        return prInquiryService.fetchPrs(owner, repo, token);
    }

}
