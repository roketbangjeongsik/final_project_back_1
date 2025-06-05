package com.finalproject.backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;
//응답 스트림을 여러 번 읽을 수 있도록
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class RestTemplateConfig {

    private static final Logger logger = LoggerFactory.getLogger(RestTemplateConfig.class);

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .requestFactory(() -> new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()))
                .additionalInterceptors(loggingInterceptor())
                .build();
    }

    private ClientHttpRequestInterceptor loggingInterceptor() {
        return (request, body, execution) -> {
            // 요청 로그
            logger.info("➡️ Request URI: {}", request.getURI());
            logger.info("➡️ Method: {}", request.getMethod());
            logger.info("➡️ Headers: {}", maskSensitiveHeaders(request.getHeaders()));
            logger.info("➡️ Body: {}", new String(body, StandardCharsets.UTF_8));

            ClientHttpResponse response = execution.execute(request, body);

            // 응답 로그
            StringBuilder inputStringBuilder = new StringBuilder();
            try (BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    inputStringBuilder.append(line);
                }
            }

            logger.info("⬅️ Status Code: {}", response.getStatusCode());
            logger.info("⬅️ Headers: {}", response.getHeaders());
            logger.info("⬅️ Body: {}", inputStringBuilder);

            return response;
        };
    }

    /**
     * 민감한 헤더 값 마스킹 유틸 함수
     */
    private Map<String, Object> maskSensitiveHeaders(HttpHeaders headers) {
        Map<String, Object> masked = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            String key = entry.getKey();
            List<String> value = entry.getValue();

            if (key.equalsIgnoreCase("Authorization") || key.equalsIgnoreCase("Cookie")) {
                masked.put(key, "****"); // 마스킹 처리
            } else {
                masked.put(key, value);
            }
        }
        return masked;
    }
}
