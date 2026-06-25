package flores.eternas.backend.controller;

import flores.eternas.backend.services.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author esteban
 * Endpoint de diagnostico para verificar la configuracion y envio de emails.
 * Solo accesible por administradores.
 */
@RestController
@RequestMapping("/api/admin/email-test")
public class EmailTestController {

    private final EmailService emailService;

    @Value("${MAIL_FROM:}")
    private String mailFrom;

    @Value("${spring.mail.host:}")
    private String mailHost;

    @Value("${spring.mail.port:}")
    private String mailPort;

    public EmailTestController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> diagnosticar(@RequestParam String to) {
        Map<String, Object> result = new LinkedHashMap<>();

        result.put("mailHost", mailHost != null && !mailHost.isBlank() ? mailHost : "NO CONFIGURADO");
        result.put("mailPort", mailPort != null && !mailPort.isBlank() ? mailPort : "NO CONFIGURADO");
        result.put("mailFrom", mailFrom != null && !mailFrom.isBlank() ? mailFrom : "NO CONFIGURADO");

        if (mailFrom == null || mailFrom.isBlank()) {
            result.put("status", "SIN_CONFIGURAR");
            result.put("message", "MAIL_FROM no esta configurado. Los emails no se envian.");
            return ResponseEntity.ok(result);
        }

        if (mailHost == null || mailHost.isBlank()) {
            result.put("status", "SIN_CONFIGURAR");
            result.put("message", "spring.mail.host no esta configurado.");
            return ResponseEntity.ok(result);
        }

        try {
            String asunto = "Test de email - Flores Eternas LP";
            String cuerpo = "<h2>Email de prueba</h2>"
                    + "<p>Si recibes este mensaje, el envio de emails esta funcionando correctamente.</p>"
                    + "<p>Fecha: " + java.time.LocalDateTime.now() + "</p>";
            emailService.enviarEmail(to, asunto, cuerpo);
            result.put("status", "ENVIO_INTENTADO");
            result.put("message", "Se intento enviar email a " + to + ". Revisa la consola/logs para confirmar.");
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("error", e.getMessage());
        }

        return ResponseEntity.ok(result);
    }
}
