package com.alexpetro.eadp.service;

import com.alexpetro.eadp.dto.DocumentCreateRequest;
import com.alexpetro.eadp.dto.DocumentResponse;
import com.alexpetro.eadp.entity.Document;
import com.alexpetro.eadp.repository.DocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private TextExtractionService textExtractionService;

    @Mock
    private AiSummaryService aiSummaryService;

    private DocumentService documentService;

    @BeforeEach
    void setUp() {
        documentService = new DocumentService(
                documentRepository,
                textExtractionService,
                aiSummaryService
        );
    }

    @Test
    void shouldCreateDocument() {
        DocumentCreateRequest request = new DocumentCreateRequest();

        /*
         * Falls dein DTO aktuell keine Setter besitzt,
         * ergänzen wir diese gleich im nächsten Schritt.
         */
        request.setFilename("contract.pdf");
        request.setContentType("application/pdf");
        request.setSummary("Contract summary");

        Document savedDocument = new Document();
        savedDocument.setId(1L);
        savedDocument.setFilename("contract.pdf");
        savedDocument.setContentType("application/pdf");
        savedDocument.setSummary("Contract summary");

        when(documentRepository.save(any(Document.class)))
                .thenReturn(savedDocument);

        DocumentResponse response =
                documentService.createDocument(request);

        assertEquals(1L, response.getId());
        assertEquals("contract.pdf", response.getFilename());
        assertEquals("application/pdf", response.getContentType());
        assertEquals("Contract summary", response.getSummary());

        verify(documentRepository).save(any(Document.class));
    }
}