package ru.fintech.translator.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.fintech.translator.dto.TranslationRequest;
import ru.fintech.translator.dto.TranslationRequestDto;
import ru.fintech.translator.exception.TranslationException;
import ru.fintech.translator.service.TranslationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/api/translate")
@RequiredArgsConstructor
public class TranslationController {

    private final TranslationService translationService;

    @PostMapping
    public ResponseEntity<?> translate(@RequestBody TranslationRequest request, @RequestHeader("X-Forwarded-For") String ipAddress) {
        try {
            String translatedText = translationService.translate(request.text(), request.sourceLang(), request.getTargetLang(), ipAddress);
            return ResponseEntity.ok(translatedText);
        } catch (TranslationException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }
}
