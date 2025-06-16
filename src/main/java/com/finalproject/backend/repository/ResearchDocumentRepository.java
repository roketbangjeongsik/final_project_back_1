package com.finalproject.backend.repository;

import com.finalproject.backend.entity.ResearchDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResearchDocumentRepository extends JpaRepository<ResearchDocument, Integer> {
    // 기본 CRUD 기능 및 필요 시 커스텀 쿼리 작성 가능
}