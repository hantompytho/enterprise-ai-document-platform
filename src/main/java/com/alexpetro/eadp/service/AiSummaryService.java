package com.alexpetro.eadp.service;

import org.springframework.stereotype.Service;

@Service
public class AiSummaryService {

    public String summarize(String text) {
        if (text == null || text.isBlank()) {
            return "No text content found";
        }

        String preview = text.length() > 500
                ? text.substring(0, 500)
                : text;

        return "AI Summary placeholder: " + preview;
    }
}