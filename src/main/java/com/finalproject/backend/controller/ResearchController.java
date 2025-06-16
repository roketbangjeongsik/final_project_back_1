package com.finalproject.backend.controller;

import com.finalproject.backend.dto.research.DocumentDTO;
import com.finalproject.backend.service.ResearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/research")
@RequiredArgsConstructor
public class ResearchController {

    private final ResearchService researchService;

    // 검색 쿼리만 받아 FastAPI에 전달 후, DB에서 결과 조회
    @GetMapping("/search")
    public ResponseEntity<List<DocumentDTO>> search(
            @RequestParam String query) {

        List<Integer> docIds = researchService.searchViaPythonServer(query);
        List<DocumentDTO> documents = researchService.findDocumentsByIds(docIds);
        return ResponseEntity.ok(documents);
    }
}
