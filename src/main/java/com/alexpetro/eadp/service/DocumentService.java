package com.alexpetro.eadp.service;

import com.alexpetro.eadp.entity.Document;
import com.alexpetro.eadp.repository.DocumentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    public Document createDocument(Document document) {
        return documentRepository.save(document);
    }
}