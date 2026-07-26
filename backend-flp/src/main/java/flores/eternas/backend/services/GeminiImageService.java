package flores.eternas.backend.services;

import flores.eternas.backend.exception.QuotaExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author demonicp
 * Servicio para generacion de imagenes usando IA.
 * Intenta primero con Gemini 2.5 Flash; si falla por cuota, usa Hugging Face (FLUX.1-dev).
 */
@Service
public class GeminiImageService {

    private static final Logger log = LoggerFactory.getLogger(GeminiImageService.class);
    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-image:generateContent";
    private static final String HF_URL =
            "https://api-inference.huggingface.co/models/black-forest-labs/FLUX.1-dev";

    private final String apiKey;
    private final String hfApiKey;
    private final RestTemplate restTemplate;
    private final ConcurrentHashMap<String, Boolean> tokensUsados = new ConcurrentHashMap<>();

    public GeminiImageService(
            @Value("${GEMINI_API_KEY:}") String apiKey,
            @Value("${HF_API_KEY:}") String hfApiKey) {
        this.apiKey = apiKey;
        this.hfApiKey = hfApiKey;
        this.restTemplate = new RestTemplate();
    }

    /**
     * @author demonicp
     * Genera una imagen usando Gemini. Si falla por cuota, usa Hugging Face como fallback.
     * Cada token de sesion solo puede generar una imagen.
     */
    public String generarImagen(String prompt, String sesionToken) {
        if (sesionToken == null || sesionToken.isBlank()) {
            throw new RuntimeException("Token de sesion requerido");
        }
        if (tokensUsados.putIfAbsent(sesionToken, true) != null) {
            throw new RuntimeException("Tu sesion expiro. Vuelve a seleccionar las flores desde el catalogo.");
        }

        if (apiKey == null || apiKey.isBlank()) {
            throw new RuntimeException("GEMINI_API_KEY no esta configurada en el .env");
        }

        try {
            return generarConGemini(prompt);
        } catch (QuotaExceededException e) {
            log.warn("Cuota de Gemini agotada, intentando fallback con Hugging Face...");
            return generarConHuggingFace(prompt);
        }
    }

    /**
     * @author demonicp
     * Llama a Gemini 2.5 Flash para generar una imagen.
     * Si detecta HTTP 429 o RESOURCE_EXHAUSTED, lanza QuotaExceededException.
     */
    private String generarConGemini(String prompt) {
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

            if (response.getStatusCode().value() == 429) {
                throw new QuotaExceededException("Gemini respondio con HTTP 429 (quota excedida)");
            }

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new RuntimeException("Error llamando a Gemini API: " + response.getStatusCode());
            }

            Map<String, Object> body = response.getBody();

            if (body.containsKey("error")) {
                Map<String, Object> error = (Map<String, Object>) body.get("error");
                String message = error != null ? (String) error.get("message") : "";
                if (message != null && message.contains("RESOURCE_EXHAUSTED")) {
                    throw new QuotaExceededException("Gemini: " + message);
                }
                throw new RuntimeException("Error de Gemini: " + message);
            }

            List<Map<String, Object>> candidates = (List<Map<String, Object>>) body.get("candidates");
            if (candidates == null || candidates.isEmpty()) {
                throw new RuntimeException("Gemini no retorno candidatos");
            }

            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            if (content == null) {
                throw new RuntimeException("Gemini no retorno contenido");
            }

            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            if (parts == null) {
                throw new RuntimeException("Gemini no retorno parts");
            }

            for (Map<String, Object> part : parts) {
                if (part.containsKey("inlineData")) {
                    Map<String, String> inlineData = (Map<String, String>) part.get("inlineData");
                    String mimeType = inlineData.getOrDefault("mimeType", "image/png");
                    String base64Data = inlineData.get("data");
                    return "data:" + mimeType + ";base64," + base64Data;
                }
            }

            throw new RuntimeException("Gemini no genero una imagen en la respuesta");

        } catch (QuotaExceededException e) {
            throw e;
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 429) {
                throw new QuotaExceededException("Gemini respondio con HTTP 429 (quota excedida)");
            }
            throw e;
        } catch (Exception e) {
            log.error("Error al generar imagen con Gemini: {}", e.getMessage(), e);
            throw new RuntimeException("Error al generar imagen: " + e.getMessage(), e);
        }
    }

    /**
     * @author demonicp
     * Llama a Hugging Face Inference API (FLUX.1-dev) como fallback.
     * La respuesta son bytes crudos que se convierten a base64.
     */
    private String generarConHuggingFace(String prompt) {
        if (hfApiKey == null || hfApiKey.isBlank()) {
            throw new RuntimeException("HF_API_KEY no esta configurada en el .env — no hay fallback disponible");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(hfApiKey);

        Map<String, String> requestBody = Map.of("inputs", prompt);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    HF_URL, HttpMethod.POST, entity, byte[].class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new RuntimeException("Error llamando a Hugging Face: " + response.getStatusCode());
            }

            byte[] imageBytes = response.getBody();
            String base64Data = java.util.Base64.getEncoder().encodeToString(imageBytes);
            return "data:image/png;base64," + base64Data;

        } catch (Exception e) {
            log.error("Error al generar imagen con Hugging Face: {}", e.getMessage(), e);
            throw new RuntimeException("Error al generar imagen con fallback: " + e.getMessage(), e);
        }
    }
}