package com.finalproject.backend.dto.GTP;

import java.util.List;

public record ChatCompletionResponse(
        String id,
        List<ChatChoice> choices
) { }
