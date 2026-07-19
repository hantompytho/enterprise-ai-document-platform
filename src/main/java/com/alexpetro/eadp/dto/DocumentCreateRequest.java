package com.alexpetro.eadp.dto;

import jakarta.validation.constraints.NotBlank;

public class DocumentCreateRequest {

    @NotBlank(message = "Filename is required")
    private String filename;

    @NotBlank(message = "Content type is required")
    private String contentType;

    private String summary;

    public String getFilename() {
        return filename;
    }

    public String getContentType() {
        return contentType;
    }

    public String getSummary() {
        return summary;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}