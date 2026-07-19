package com.alexpetro.eadp.service;

import com.alexpetro.eadp.dto.DocumentCreateRequest;
import com.alexpetro.eadp.dto.DocumentResponse;
import com.alexpetro.eadp.entity.Document;
import com.alexpetro.eadp.entity.Role;
import com.alexpetro.eadp.entity.User;
import com.alexpetro.eadp.repository.DocumentRepository;
import com.alexpetro.eadp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TextExtractionService textExtractionService;

    @Mock
    private AiSummaryService aiSummaryService;

    private DocumentService documentService;

    @BeforeEach
    void setUp() {
        documentService = new DocumentService(
                documentRepository,
                userRepository,
                aiSummaryService,
                textExtractionService
        );
    }

    @Test
    void shouldCreateDocument() {
        String email = "alex@example.com";

        DocumentCreateRequest request = new DocumentCreateRequest();
        request.setFilename("contract.pdf");
        request.setContentType("application/pdf");
        request.setSummary("Contract summary");

        User user = new User();
        user.setEmail(email);
        user.setPassword("password");
        user.setRole(Role.USER);

        when(userRepository.findByEmailIgnoreCase(email))
                .thenReturn(Optional.of(user));

        when(documentRepository.save(any(Document.class)))
                .thenAnswer(invocation -> {
                    Document document = invocation.getArgument(0);
                    document.setId(1L);
                    return document;
                });

        DocumentResponse response =
                documentService.createDocument(request, email);

        assertEquals(1L, response.getId());
        assertEquals("contract.pdf", response.getFilename());
        assertEquals("application/pdf", response.getContentType());
        assertEquals("Contract summary", response.getSummary());

        verify(userRepository).findByEmailIgnoreCase(email);
        verify(documentRepository).save(
                org.mockito.ArgumentMatchers.argThat(document ->
                        document.getOwner() == user
                )
        );
    }
}