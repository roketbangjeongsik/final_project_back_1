package com.finalproject.backend.service;

import com.finalproject.backend.dto.GTP.ChatChoice;
import com.finalproject.backend.dto.GTP.ChatCompletionResponse;
import com.finalproject.backend.dto.GTP.ChatMessage;
import com.finalproject.backend.dto.GTP.SafeChatMessage;
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

    private String decodeEscapedDiff(String diff){
        return diff
                .replace("\\n", "\n")
                .replace("\\t", "\t")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }

    private static final ChatMessage SYSTEM_MESSAGE = new ChatMessage(
            "system",
            "당신은 다양한 언어에 능숙한 시니어 개발자입니다."
    );

    private static final String REVIEW_PROMPT =
            """
            제공된 코드의 주요 기능, 동작 흐름, 그리고 코드의 목적을 이해하기 쉽게 설명해주세요.
    
            ```diff
            %s
            ```
            """;

    private static final String REFACTOR_PROMPT =
            """
            제공된 코드의 가독성, 재사용성을 고려하여 리팩토링이 필요한 부분을 지적하고,
            개선 방향과 예시 코드를 함께 제안해주세요.
    
            ```diff
            %s
            ```
            """;

    public ChatCompletionResponse reviewPullRequest(String diff) {
        String decodeDiff = decodeEscapedDiff(diff);
        ChatMessage user = new ChatMessage("user", String.format(REVIEW_PROMPT, decodeDiff));
        return callOpenAI(SYSTEM_MESSAGE, user);
    }

    public ChatCompletionResponse refactorPullRequest(String diff) {
        String decodeDiff = decodeEscapedDiff(diff);
        ChatMessage user = new ChatMessage("user", String.format(REFACTOR_PROMPT, decodeDiff));
        return callOpenAI(SYSTEM_MESSAGE, user);
    }

    private ChatCompletionResponse callOpenAI(ChatMessage system, ChatMessage user) {
        var sysMsg = new SafeChatMessage("system", system.content());
        var userMsg = new SafeChatMessage("user", user.content());

        ChatCompletionRequest req = ChatCompletionRequest.builder()
                .model("gpt-4o-mini")
                .messages(List.of(sysMsg, userMsg))
                .temperature(0.2)
                .build();

        ChatCompletionResult res = openAiService.createChatCompletion(req);

        List<ChatChoice> choices = res.getChoices().stream()
                .map(c -> new ChatChoice(
                        c.getIndex(),
                        new ChatMessage( // ✅ 다시 원래 타입으로 감싸기
                                c.getMessage().getRole(),
                                c.getMessage().getContent()
                        ),
                        c.getFinishReason()
                ))
                .collect(Collectors.toList());

        return new ChatCompletionResponse(res.getId(), choices);
    }
}
