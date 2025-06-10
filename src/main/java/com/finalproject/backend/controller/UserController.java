package com.finalproject.backend.controller;

import com.finalproject.backend.dto.user.UserResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("/api/v1")
@RestController
public class UserController {
    @GetMapping("/user/me")

    public UserResponse user(
            @Parameter(hidden = true)
            @AuthenticationPrincipal OAuth2User principal
    ) {
        String login     = principal.getAttribute("login");
        String name      = principal.getAttribute("name");
        String avatarUrl = principal.getAttribute("avatar_url");

        return new UserResponse(login, name, avatarUrl);
    }
    @PostMapping("/user/logout")
    public ResponseEntity<Void> logout(
            @Parameter(hidden = true)
            HttpServletRequest request,
            @Parameter(hidden = true)
            HttpServletResponse response) {

        request.getSession().invalidate();
        SecurityContextHolder.clearContext();
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }
}
