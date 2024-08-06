package ru.fintech.translator.service;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.fintech.translator.dto.TranslationResponse;
import ru.fintech.translator.repository.TranslationRequestRepository;
import ru.fintech.translator.exception.TranslationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TranslationServiceImpl implements TranslationService {

    private final RestTemplate restTemplate;
    private final TranslationRequestRepository translationRequestRepository;

    @Value("${translation.api.url}")
    private String apiUrl;

    @Value("${translation.api.key}")
    private String apiKey;

    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Override
    public String translate(String sourceText, String sourceLang, String targetLang, String ipAddress) {
        List<String> words = Arrays.asList(sourceText.split("\\s+"));

        List<Future<String>> futures = words.stream()
                .map(word -> executorService.submit(() -> translateWord(word, sourceLang, targetLang)))
                .collect(Collectors.toList());

        String translatedText = futures.stream()
                .map(this::getFutureResult)
                .collect(Collectors.joining(" "));

        TranslationRequest translationRequest = new TranslationRequest();
        translationRequest.setIpAddress(ipAddress);
        translationRequest.setSourceText(sourceText);
        translationRequest.setTranslatedText(translatedText);
        translationRequest.setRequestTime(LocalDateTime.now());

        translationRequestRepository.save(translationRequest);

        return translatedText;
    }

    private String translateWord(String word, String sourceLang, String targetLang) {
        String url = String.format("%s?source=%s&target=%s&q=%s&key=%s", apiUrl, sourceLang, targetLang, word, apiKey);
        try {
            TranslationResponse response = restTemplate.getForObject(url, TranslationResponse.class);
            if (response == null) {
                throw new TranslationException("Empty response from translation service", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return response.getTranslatedText();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new TranslationException("Invalid source or target language", HttpStatus.BAD_REQUEST);
            } else if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new TranslationException("Unauthorized access to translation service", HttpStatus.UNAUTHORIZED);
            } else if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw new TranslationException("Access to translation service is forbidden", HttpStatus.FORBIDDEN);
            } else if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new TranslationException("Translation service not found", HttpStatus.NOT_FOUND);
            } else {
                throw new TranslationException("Error accessing translation service", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (RestClientException e) {
            throw new TranslationException("Error accessing translation service", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String getFutureResult(Future<String> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new TranslationException("Error processing translation", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

// TODO Валидацию лучше перенести как в хелпере (зависимость Spring Boot Validation)
