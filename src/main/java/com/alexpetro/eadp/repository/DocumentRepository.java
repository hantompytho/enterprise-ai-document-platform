package com.alexpetro.eadp.repository;

import com.alexpetro.eadp.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByFilenameContainingIgnoreCaseOrSummaryContainingIgnoreCase(
            String filename,
            String summary
    );
}