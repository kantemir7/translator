package ru.fintech.translator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleTranslateResponse(
        @JsonProperty("data")
        Data data) {
}
