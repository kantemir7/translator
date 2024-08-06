package ru.fintech.translator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Translation(
        @JsonProperty("translatedText")
        String translatedText) {
}
