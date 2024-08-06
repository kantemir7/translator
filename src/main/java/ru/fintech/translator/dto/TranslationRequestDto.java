package ru.fintech.translator.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(description = "Запрос на перевод текста")
public record TranslationRequestDto(
        @ApiModelProperty(value = "Исходный язык", example = "en", required = true)
        String sourceLang,

        @ApiModelProperty(value = "Целевой язык", example = "ru", required = true)
        String targetLang,

        @ApiModelProperty(value = "Исходный текст для перевода", example = "Hello there", required = true)
        String sourceText
) {
}