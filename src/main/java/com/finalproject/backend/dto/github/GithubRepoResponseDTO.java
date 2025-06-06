package com.finalproject.backend.dto.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GithubRepoResponseDTO {
    private String name;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("private")
    private boolean isPrivate;

    private Owner owner;

    @Data
    public static class Owner {
        @JsonProperty("login")
        private String login;
    }
}
