package com.alexpetro.eadp.repository;

import com.alexpetro.eadp.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository
        extends JpaRepository<Document, Long> {

}