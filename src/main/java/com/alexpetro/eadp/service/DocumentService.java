package com.alexpetro.eadp.service;

import com.alexpetro.eadp.dto.DocumentCreateRequest;
import com.alexpetro.eadp.dto.DocumentResponse;
import com.alexpetro.eadp.entity.Document;
import com.alexpetro.eadp.repository.DocumentRepository;
import org.springframework.stereotype.Service;
import com.alexpetro.eadp.exception.DocumentNotFoundException;
import com.alexpetro.eadp.dto.DocumentUpdateRequest;

import java.util.List;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public List<DocumentResponse> getAllDocuments() {
        return documentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public DocumentResponse createDocument(DocumentCreateRequest request) {

        Document document = new Document();

        document.setFilename(request.getFilename());
        document.setContentType(request.getContentType());
        document.setSummary(request.getSummary());

        Document savedDocument = documentRepository.save(document);

        return mapToResponse(savedDocument);
    }

    private DocumentResponse mapToResponse(Document document) {
        return new DocumentResponse(
                document.getId(),
                document.getFilename(),
                document.getContentType(),
                document.getSummary(),
                document.getCreatedAt()
        );
    }

    public DocumentResponse getDocumentById(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException(id));

        return mapToResponse(document);
    }

    public DocumentResponse updateDocument(Long id, DocumentUpdateRequest request) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException(id));

        document.setFilename(request.getFilename());
        document.setContentType(request.getContentType());
        document.setSummary(request.getSummary());

        Document updatedDocument = documentRepository.save(document);

        return mapToResponse(updatedDocument);
    }
}