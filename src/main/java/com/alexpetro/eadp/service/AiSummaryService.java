package com.alexpetro.eadp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class AiSummaryService {

    private final RestClient restClient;
    private final String apiKey;
    private final String model;

    public AiSummaryService(
            @Value("${openai.api.key}") String apiKey,
            @Value("${openai.api.url}") String apiUrl,
            @Value("${openai.model}") String model
    ) {
        this.apiKey = apiKey;
        this.model = model;
        this.restClient = RestClient.builder()
                .baseUrl(apiUrl)
                .build();
    }

    public String summarize(String text) {
        if (text == null || text.isBlank()) {
            return "No text content found";
        }

        if (apiKey == null || apiKey.isBlank()) {
            return fallbackSummary(text);
        }

        Map<String, Object> request = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of(
                                "role", "system",
                                "content", "You summarize business documents clearly and concisely."
                        ),
                        Map.of(
                                "role", "user",
                                "content", "Summarize this document in 3 bullet points:\n\n" + text
                        )
                ),
                "temperature", 0.2
        );

        try {
            Map response = restClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + apiKey)
                    .body(request)
                    .retrieve()
                    .body(Map.class);

            List choices = (List) response.get("choices");
            Map firstChoice = (Map) choices.get(0);
            Map message = (Map) firstChoice.get("message");

            return (String) message.get("content");
        } catch (Exception exception) {
            return fallbackSummary(text);
        }
    }

    private String fallbackSummary(String text) {
        String preview = text.length() > 500
                ? text.substring(0, 500)
                : text;

        return "AI Summary placeholder: " + preview;
    }
}