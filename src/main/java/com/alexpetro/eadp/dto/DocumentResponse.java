package com.alexpetro.eadp.dto;

import java.time.LocalDateTime;

public class DocumentResponse {

    private Long id;
    private String filename;
    private String contentType;
    private String summary;
    private LocalDateTime createdAt;

    public DocumentResponse(Long id, String filename, String contentType, String summary, LocalDateTime createdAt) {
        this.id = id;
        this.filename = filename;
        this.contentType = contentType;
        this.summary = summary;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getFilename() {
        return filename;
    }

    public String getContentType() {
        return contentType;
    }

    public String getSummary() {
        return summary;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}