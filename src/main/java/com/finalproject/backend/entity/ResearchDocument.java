package com.finalproject.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "documents", 
       indexes = @Index(name = "idx_documents_url", columnList = "url", unique = true))
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResearchDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 1000)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String contents;

    @Column(length = 1000, unique = true)
    private String url;

    private LocalDateTime datetime;

    public ResearchDocument(int id, String title, String contents, String url, LocalDateTime datetime) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.url = url;
        this.datetime = datetime;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
    }
}
