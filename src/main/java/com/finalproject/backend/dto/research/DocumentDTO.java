package com.finalproject.backend.dto.research;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDTO {

    private Integer id;

    private String title;

    private String contents;

    private String url;

    // JSON 필드명이 "datetime"이라면 이 어노테이션 꼭 붙이기
    @JsonProperty("datetime")
    private LocalDateTime datetime;
}
