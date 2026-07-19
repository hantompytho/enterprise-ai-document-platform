package com.alexpetro.eadp.repository;

import com.alexpetro.eadp.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    Page<Document> findAllByOwnerEmailIgnoreCase(
            String email,
            Pageable pageable
    );

    Optional<Document> findByIdAndOwnerEmailIgnoreCase(
            Long id,
            String email
    );

    @Query("""
            SELECT d
            FROM Document d
            WHERE LOWER(d.owner.email) = LOWER(:email)
              AND (
                  LOWER(d.filename) LIKE LOWER(CONCAT('%', :query, '%'))
                  OR LOWER(COALESCE(d.summary, ''))
                     LIKE LOWER(CONCAT('%', :query, '%'))
              )
            """)
    Page<Document> searchByOwner(
            @Param("email") String email,
            @Param("query") String query,
            Pageable pageable
    );

    List<Document> findByFilenameContainingIgnoreCaseOrSummaryContainingIgnoreCase(
            String filename,
            String summary
    );
}