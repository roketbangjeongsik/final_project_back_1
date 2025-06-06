package com.finalproject.backend.dto.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GithubRepoListItemDTO {

    private String name;

    @JsonProperty("full_name")
    private String fullName;

    private String language;

    @JsonProperty("updated_at")
    private String updatedAt;

    @JsonProperty("private")
    private boolean isPrivateRaw;

    private Owner owner;

    public String getVisibility() {
        return isPrivateRaw ? "private" : "public";
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Owner {

        @JsonProperty("login")
        private String name;
    }
}
