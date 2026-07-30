package flores.eternas.backend.controller;

import flores.eternas.backend.services.GeminiImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/gemini")
public class GeminiController {

    private static final Logger log = LoggerFactory.getLogger(GeminiController.class);

    private final GeminiImageService geminiImageService;

    public GeminiController(GeminiImageService geminiImageService) {
        this.geminiImageService = geminiImageService;
    }

    @PostMapping("/generar-imagen")
    public ResponseEntity<Map<String, String>> generarImagen(@RequestBody Map<String, String> body) {
        String prompt = body.get("prompt");
        if (prompt == null || prompt.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El prompt es obligatorio"));
        }

        String sesionToken = body.get("sesionToken");
        if (sesionToken == null || sesionToken.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El token de sesion es obligatorio"));
        }

        try {
            String imageUrl = geminiImageService.generarImagen(prompt, sesionToken);
            return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
        } catch (Exception e) {
            log.error("Error generando imagen con IA: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}