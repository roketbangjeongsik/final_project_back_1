package com.finalproject.backend.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("/api/v1")
@RestController
public class UserController {
    @SecurityRequirement(name = "github-oauth")
    @GetMapping("/user/me")
    public Map<String,Object> user(
            @AuthenticationPrincipal OAuth2User principal,
            @RegisteredOAuth2AuthorizedClient("github") OAuth2AuthorizedClient client
    ) {
        OAuth2AccessToken accessToken = client.getAccessToken();
        System.out.println("🔑 Access Token: " + accessToken.getTokenValue());
        return principal.getAttributes();
    }
}
