package com.alexpetro.eadp.service;

import com.alexpetro.eadp.dto.DocumentCreateRequest;
import com.alexpetro.eadp.dto.DocumentResponse;
import com.alexpetro.eadp.entity.Document;
import com.alexpetro.eadp.repository.DocumentRepository;
import org.springframework.stereotype.Service;
import com.alexpetro.eadp.exception.DocumentNotFoundException;
import com.alexpetro.eadp.dto.DocumentUpdateRequest;
import org.springframework.web.multipart.MultipartFile;
import com.alexpetro.eadp.exception.InvalidFileException;
import java.util.Set;

import java.util.List;

@Service
public class DocumentService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "application/pdf",
            "text/plain",
            "image/jpeg",
            "image/png"
    );

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

    public void deleteDocument(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException(id));

        documentRepository.delete(document);
    }

    public DocumentResponse uploadDocument(MultipartFile file) {

        if (file.isEmpty()) {
            throw new InvalidFileException(
                    "Uploaded file must not be empty"
            );
        }

        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new InvalidFileException(
                    "Unsupported file type: " + file.getContentType()
            );
        }

        Document document = new Document();

        document.setFilename(file.getOriginalFilename());
        document.setContentType(file.getContentType());
        document.setSummary("AI summary not generated yet");

        Document savedDocument = documentRepository.save(document);

        return mapToResponse(savedDocument);
    }
}