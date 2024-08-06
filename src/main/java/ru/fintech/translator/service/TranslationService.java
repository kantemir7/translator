package ru.fintech.translator.service;

public interface TranslationService {
    String translate(String text, String sourceLang, String targetLang, String ipAddress) throws Exception;
}
