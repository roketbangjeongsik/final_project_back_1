package com.finalproject.backend.dto.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;
import java.time.OffsetDateTime;

@Data
public class KakaoBlogSearchResponse {
    private Meta meta;
    private List<Document> documents;

    @Data
    public static class Meta {
        @JsonProperty("total_count")
        private int totalCount;
        
        @JsonProperty("pageable_count")
        private int pageableCount;
        
        @JsonProperty("is_end")
        private boolean isEnd;
    }

    @Data
    public static class Document {
        private String title;
        private String contents;
        private String url;
        
        @JsonProperty("datetime")
        private OffsetDateTime datetime;
    }
} 