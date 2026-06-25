package flores.eternas.backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final String from;

    public EmailService(JavaMailSender mailSender,
                        @Value("${MAIL_FROM:}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    public void enviarEmail(String destino, String asunto, String cuerpoHtml) {
        if (from == null || from.isBlank()) {
            log.warn("MAIL_FROM no configurado. No se envió el email a {}", destino);
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(destino);
            helper.setSubject(asunto);
            helper.setText(cuerpoHtml, true);
            mailSender.send(message);
            log.info("Email enviado a {}", destino);
        } catch (Exception e) {
            log.error("Error al enviar email a {}: {}", destino, e.getMessage());
        }
    }
}
