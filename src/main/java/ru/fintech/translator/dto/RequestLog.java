package ru.fintech.translator.dto;

import nonapi.io.github.classgraph.json.Id;

public record RequestLog(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        Long id,
        String text,
        String sourceLang,
        String targetLang,
        String translatedText,
        String ipAddress,
        LocalDateTime requestDate
) {
}
