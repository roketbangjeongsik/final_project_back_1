package com.finalproject.backend.dto.GTP;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.theokanning.openai.completion.chat.ChatMessage;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SafeChatMessage extends ChatMessage {
    public SafeChatMessage(String role, String content) {
        super(role, content);
    }
}
