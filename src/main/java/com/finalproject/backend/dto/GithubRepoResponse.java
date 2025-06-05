package com.finalproject.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GithubRepoResponse {
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
