package com.alexpetro.eadp.controller;

import com.alexpetro.eadp.dto.DocumentCreateRequest;
import com.alexpetro.eadp.dto.DocumentResponse;
import com.alexpetro.eadp.service.DocumentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.alexpetro.eadp.dto.DocumentUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }


    @GetMapping
    public List<DocumentResponse> getAllDocuments() {
        return documentService.getAllDocuments();
    }


    @PostMapping
    public DocumentResponse createDocument(
            @Valid @RequestBody DocumentCreateRequest request
    ) {
        return documentService.createDocument(request);
    }

    @GetMapping("/search")
    public List<DocumentResponse> searchDocuments(@RequestParam String query) {
        return documentService.searchDocuments(query);
    }

    @GetMapping("/{id}")
    public DocumentResponse getDocumentById(@PathVariable Long id) {
        return documentService.getDocumentById(id);
    }

    @PutMapping("/{id}")
    public DocumentResponse updateDocument(
            @PathVariable Long id,
            @Valid @RequestBody DocumentUpdateRequest request
    ) {
        return documentService.updateDocument(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
    }

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public DocumentResponse uploadDocument(@RequestParam("file") MultipartFile file) {
        return documentService.uploadDocument(file);
    }
}