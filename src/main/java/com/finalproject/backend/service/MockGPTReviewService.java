package com.finalproject.backend.service;

import com.finalproject.backend.dto.GTP.ChatChoice;
import com.finalproject.backend.dto.GTP.ChatCompletionResponse;
import com.finalproject.backend.dto.GTP.ChatMessage;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
@Profile("dev")
public class MockGPTReviewService extends GPTReviewService{

    public MockGPTReviewService() {
        super(/* dummy */ null);
    }

    @Override
    public ChatCompletionResponse refactorPullRequest(String diff) {
        ChatMessage msg = new ChatMessage("assistant",
                "샘플 리팩토링 제안: 이 메서드는 가독성을 위해 ...");
        ChatChoice choice = new ChatChoice(0, msg, "stop");
        return new ChatCompletionResponse("mock-id-123", List.of(choice));
    }
}
