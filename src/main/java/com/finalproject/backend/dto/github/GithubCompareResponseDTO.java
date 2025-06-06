package com.finalproject.backend.dto.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GithubCompareResponseDTO {

    @JsonProperty("base_commit")
    private CommitSummary baseCommit;

    @JsonProperty("merge_base_commit")
    private CommitSummary mergeBaseCommit;

    @JsonProperty("ahead_by")
    private int aheadBy;

    @JsonProperty("behind_by")
    private int behindBy;

    private List<Commit> commits;

    private List<ChangedFile> files;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CommitSummary {
        private String sha;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Commit {
        private String sha;

        @JsonProperty("commit")
        private CommitInfo commitInfo;

        @JsonProperty("html_url")
        private String htmlUrl;

        @Getter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class CommitInfo {
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

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChangedFile {
        private String filename;
        private String status;
        private int changes;
        private String patch;
    }
}
