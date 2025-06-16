package com.finalproject.backend.controller;

import com.finalproject.backend.dto.GTP.ChatCompletionResponse;
import com.finalproject.backend.dto.GTP.PatchRequest;
import com.finalproject.backend.service.GPTReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/pr")
@RequiredArgsConstructor
public class PRReviewController {
    private final GPTReviewService GPTReviewService;  // 인스턴스 주입

    @PostMapping("/review")
    public ChatCompletionResponse review(@RequestBody PatchRequest body) {
        return GPTReviewService.reviewPullRequest(body.getPatch());
    }

    @PostMapping("/refactor")
    public ChatCompletionResponse refactor(@RequestBody PatchRequest body) {
        return GPTReviewService.refactorPullRequest(body.getPatch());
    }
}
