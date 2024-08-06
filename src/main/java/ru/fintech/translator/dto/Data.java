package ru.fintech.translator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record Data(
        @JsonProperty("translations")
        List<Translation> translations
) {
}
