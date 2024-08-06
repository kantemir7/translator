package ru.fintech.translator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TranslationResponse {
    @JsonProperty("translatedText")
    private String translatedText;
}
