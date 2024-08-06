package ru.fintech.translator.service;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import ru.fintech.translator.dto.GoogleTranslateResponse;
import ru.fintech.translator.exception.TranslationException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
public class GoogleTranslationService implements TranslationService {

    @Value("${google.translate.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Override
    public String translate(String text, String sourceLang, String targetLang, String ipAddress) throws Exception {
        Future<String> futureTranslation = executorService.submit(() -> callGoogleTranslateAPI(text, sourceLang, targetLang));
        try {
            return futureTranslation.get();
        } catch (Exception e) {
            throw new TranslationException("Translation failed", 500);
        }
    }

    private String callGoogleTranslateAPI(String text, String sourceLang, String targetLang) throws Exception {
        String url = "https://translation.googleapis.com/language/translate/v2";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        Map<String, Object> body = new HashMap<>();
        body.put("q", text);
        body.put("source", sourceLang);
        body.put("target", targetLang);
        body.put("format", "text");
        body.put("key", apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<GoogleTranslateResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, GoogleTranslateResponse.class);
            GoogleTranslateResponse responseBody = response.getBody();
            if (responseBody != null && responseBody.data() != null && !responseBody.data().translations().isEmpty()) {
                return responseBody.data().translations().getFirst().translatedText();
            }
            throw new TranslationException("Translation failed", 500);
        } catch (HttpClientErrorException e) {
            // Handle client-side error (4xx)
            throw new TranslationException("Client error: " + e.getMessage(), e.getStatusCode().value());
        } catch (HttpServerErrorException e) {
            // Handle server-side error (5xx)
            throw new TranslationException("Server error: " + e.getMessage(), e.getStatusCode().value());
        } catch (Exception e) {
            throw new TranslationException("Translation API call failed: " + e.getMessage(), 500);
        }
    }
}
