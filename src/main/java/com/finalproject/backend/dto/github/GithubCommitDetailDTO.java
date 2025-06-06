package com.finalproject.backend.dto.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GithubCommitDetailDTO {

    private String sha;

    @JsonProperty("commit")
    private Commit commit;

    private Author author;

    private List<File> files;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Commit {
        private String message;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Author {
        private String login;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class File {
        private String filename;
        private int changes;
        private String patch;
    }

    // 아래 세터는 커스텀 응답 구성을 위해 사용될 수 있습니다.
    public String getMessage() {
        return commit != null ? commit.message : null;
    }

    public String getAuthor() {
        return author != null ? author.login : null;
    }
}
