package flores.eternas.backend.controller;

import flores.eternas.backend.services.GeminiImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/gemini")
public class GeminiController {

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

        try {
            String imageUrl = geminiImageService.generarImagen(prompt);
            return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}