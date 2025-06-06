package com.finalproject.backend.dto.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GithubPullRequestCommitDTO {

    private String sha;

    @JsonProperty("commit")
    private Commit commit;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Commit {
        private String message;

        private Author author;

        @Getter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Author {
            private String name;

            private String date;
        }
    }
}
