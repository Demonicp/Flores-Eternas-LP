package flores.eternas.backend;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import flores.eternas.backend.services.EmailService;

@SpringBootTest
@ActiveProfiles("test")
class EmailIntegrationTest {

    @Autowired
    private EmailService emailService;

    @Test
    void testJavaMailSenderInjected() {
        assertNotNull(emailService, "EmailService no se inyecto");
    }

    @Test
    void testEnviarEmailExitoso() {
        String testEmail = "test@example.com";
        String asunto = "Test email - " + System.currentTimeMillis();
        String cuerpo = "<h2>Test</h2><p>Este es un email de prueba.</p>";

        assertDoesNotThrow(() -> {
            emailService.enviarEmail(testEmail, asunto, cuerpo);
        }, "El envio de email no deberia lanzar excepcion");
    }

    @Test
    void testEnviarEmailSinFromNoFalla() {
        String testEmail = "test@example.com";
        String asunto = "Test sin from";
        String cuerpo = "<p>Test</p>";

        assertDoesNotThrow(() -> {
            emailService.enviarEmail(testEmail, asunto, cuerpo);
        }, "Enviar email sin MAIL_FROM no deberia lanzar excepcion");
    }
}
