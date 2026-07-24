package flores.eternas.backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class GeminiImageService {

    private static final Logger log = LoggerFactory.getLogger(GeminiImageService.class);
    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-image:generateContent";

    private final String apiKey;
    private final RestTemplate restTemplate;

    public GeminiImageService(@Value("${GEMINI_API_KEY:}") String apiKey) {
        this.apiKey = apiKey;
        this.restTemplate = new RestTemplate();
    }

    public String generarImagen(String prompt) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new RuntimeException("GEMINI_API_KEY no está configurada en el .env");
        }

        String url = GEMINI_URL + "?key=" + apiKey;

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt)))),
                "generationConfig", Map.of("responseModalities", List.of("IMAGE", "TEXT"))
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new RuntimeException("Error llamando a Gemini API: " + response.getStatusCode());
            }

            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
            if (candidates == null || candidates.isEmpty()) {
                throw new RuntimeException("Gemini no retornó candidatos");
            }

            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            if (content == null) {
                throw new RuntimeException("Gemini no retornó contenido");
            }

            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            if (parts == null) {
                throw new RuntimeException("Gemini no retornó parts");
            }

            for (Map<String, Object> part : parts) {
                if (part.containsKey("inlineData")) {
                    Map<String, String> inlineData = (Map<String, String>) part.get("inlineData");
                    String mimeType = inlineData.getOrDefault("mimeType", "image/png");
                    String base64Data = inlineData.get("data");
                    return "data:" + mimeType + ";base64," + base64Data;
                }
            }

            throw new RuntimeException("Gemini no generó una imagen en la respuesta");

        } catch (Exception e) {
            log.error("Error al generar imagen con Gemini: {}", e.getMessage(), e);
            throw new RuntimeException("Error al generar imagen: " + e.getMessage(), e);
        }
    }
}