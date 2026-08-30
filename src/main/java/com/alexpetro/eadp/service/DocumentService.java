package com.alexpetro.eadp.service;

import com.alexpetro.eadp.dto.DocumentCreateRequest;
import com.alexpetro.eadp.dto.DocumentResponse;
import com.alexpetro.eadp.entity.Document;
import com.alexpetro.eadp.entity.User;
import com.alexpetro.eadp.exception.DocumentNotFoundException;
import com.alexpetro.eadp.exception.InvalidFileException;
import com.alexpetro.eadp.repository.DocumentRepository;
import com.alexpetro.eadp.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

@Service
public class DocumentService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "application/pdf",
            "text/plain",
            "image/jpeg",
            "image/png"
    );

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final AiSummaryService aiSummaryService;
    private final TextExtractionService textExtractionService;

    public DocumentService(
            DocumentRepository documentRepository,
            UserRepository userRepository,
            AiSummaryService aiSummaryService,
            TextExtractionService textExtractionService
    ) {
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
        this.aiSummaryService = aiSummaryService;
        this.textExtractionService = textExtractionService;
    }

    public Page<DocumentResponse> getAllDocuments(
            String email,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        return documentRepository
                .findAllByOwnerEmailIgnoreCase(email, pageable)
                .map(this::mapToResponse);
    }

    public DocumentResponse getDocumentById(
            Long id,
            String email
    ) {
        Document document = findDocumentByIdAndOwner(id, email);

        return mapToResponse(document);
    }

    public DocumentResponse createDocument(
            DocumentCreateRequest request,
            String email
    ) {
        User owner = getUserByEmail(email);

        Document document = new Document();
        document.setFilename(request.getFilename());
        document.setContentType(request.getContentType());
        document.setSummary(request.getSummary());
        document.setOwner(owner);

        Document savedDocument = documentRepository.save(document);

        return mapToResponse(savedDocument);
    }

    public DocumentResponse updateDocument(
            Long id,
            DocumentCreateRequest request,
            String email
    ) {
        Document document = findDocumentByIdAndOwner(id, email);

        document.setFilename(request.getFilename());
        document.setContentType(request.getContentType());
        document.setSummary(request.getSummary());

        Document savedDocument = documentRepository.save(document);

        return mapToResponse(savedDocument);
    }

    public void deleteDocument(Long id, String email) {
        Document document = findDocumentByIdAndOwner(id, email);

        documentRepository.delete(document);
    }

    public DocumentResponse uploadDocument(
            MultipartFile file,
            String email
    ) throws IOException {
        validateFile(file);

        User owner = getUserByEmail(email);

        String extractedText = textExtractionService.extractText(file);
        String summary = aiSummaryService.summarize(extractedText);

        Document document = new Document();
        document.setFilename(file.getOriginalFilename());
        document.setContentType(file.getContentType());
        document.setData(file.getBytes());
        document.setSummary(summary);
        document.setOwner(owner);

        Document savedDocument = documentRepository.save(document);

        return mapToResponse(savedDocument);
    }

    public Page<DocumentResponse> searchDocuments(
            String email,
            String query,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        return documentRepository
                .searchByOwner(email, query, pageable)
                .map(this::mapToResponse);
    }

    public Page<DocumentResponse> getAllDocumentsForAdmin(
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        return documentRepository
                .findAll(pageable)
                .map(this::mapToResponse);
    }

    public DocumentResponse getDocumentByIdForAdmin(Long id) {
        Document document = documentRepository
                .findById(id)
                .orElseThrow(() -> new DocumentNotFoundException(id));

        return mapToResponse(document);
    }

    public void deleteDocumentForAdmin(Long id) {
        Document document = documentRepository
                .findById(id)
                .orElseThrow(() -> new DocumentNotFoundException(id));

        documentRepository.delete(document);
    }

    private Document findDocumentByIdAndOwner(
            Long id,
            String email
    ) {
        return documentRepository
                .findByIdAndOwnerEmailIgnoreCase(id, email)
                .orElseThrow(() -> new DocumentNotFoundException(id));
    }

    private User getUserByEmail(String email) {
        return userRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found")
                );
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("File must not be empty");
        }

        String contentType = file.getContentType();

        if (contentType == null
                || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new InvalidFileException(
                    "Unsupported file type: " + contentType
            );
        }
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

    public Document downloadDocument(
            Long id,
            String email
    ) {
        return findDocumentByIdAndOwner(id, email);
    }
}