package com.finalproject.backend.dto.pr;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PrResponseDTO {
    private Integer number;
    private String state;
    private String title;

    @JsonProperty("html_url")
    private String htmlUrl;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    private User user;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class User {
        private String login;

        @JsonProperty("html_url")
        private String htmlUrl;

        @JsonProperty("avatar_url")
        private String avatarUrl;
    }
}
