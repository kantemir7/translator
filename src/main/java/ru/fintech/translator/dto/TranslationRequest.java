package ru.fintech.translator.dto;

import java.time.LocalDateTime;

public record TranslationRequest(
        Long id,
        String ipAddress,
        String sourceText,
        String translatedText,
        LocalDateTime requestTime) {
}
