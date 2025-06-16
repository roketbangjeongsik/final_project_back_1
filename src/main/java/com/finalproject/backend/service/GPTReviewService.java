package com.finalproject.backend.service;

import com.finalproject.backend.dto.GTP.ChatChoice;
import com.finalproject.backend.dto.GTP.ChatCompletionResponse;
import com.finalproject.backend.dto.GTP.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GPTReviewService {

    private final OpenAiService openAiService;

    private static final ChatMessage REVIEW_SYSTEM = new ChatMessage(
            "system",
            "당신은 시니어 개발자입니다. 제공된 PR의 변경 사항을 버그·보안·스타일·모범 사례 관점에서 분석하고 지적하되, 코드 리팩토링은 하지 마세요."
    );
    private static final ChatMessage REFACTOR_SYSTEM = new ChatMessage(
            "system",
            "당신은 시니어 개발자입니다. 제공된 PR의 변경 사항을 가독성·성능·모범 사례 관점에서 개선(리팩토링)하되, 리뷰 평가는 생략하세요."
    );
    private static final String USER_TEMPLATE =
            "다음 PR의 변경 사항을 처리해 주세요:\n```diff\n%s\n```";

    public ChatCompletionResponse reviewPullRequest(String diff) {
        ChatMessage user = new ChatMessage("user", String.format(USER_TEMPLATE, diff));
        return callOpenAI(REVIEW_SYSTEM, user);
    }

    public ChatCompletionResponse refactorPullRequest(String diff) {
        ChatMessage user = new ChatMessage("user", String.format(USER_TEMPLATE, diff));
        return callOpenAI(REFACTOR_SYSTEM, user);
    }

    private ChatCompletionResponse callOpenAI(ChatMessage system, ChatMessage user) {
        var sysMsg = new com.theokanning.openai.completion.chat.ChatMessage(
                "system", system.content());
        var userMsg = new com.theokanning.openai.completion.chat.ChatMessage(
                "user",   user.content());

        ChatCompletionRequest req = ChatCompletionRequest.builder()
                .model("gpt-4o-mini")
                .messages(List.of(sysMsg, userMsg))
                .temperature(0.2)
                .build();

        ChatCompletionResult res = openAiService.createChatCompletion(req);

        List<ChatChoice> choices = res.getChoices().stream()
                .map(c -> new ChatChoice(
                        c.getIndex(),
                        new ChatMessage(c.getMessage().getRole(), c.getMessage().getContent()),
                        c.getFinishReason()
                ))
                .collect(Collectors.toList());

        return new ChatCompletionResponse(res.getId(), choices);
    }
}
