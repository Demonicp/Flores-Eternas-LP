package flores.eternas.backend.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import static net.logstash.logback.argument.StructuredArguments.kv;

class AdminLoginLoggerTest {

    private static final Logger log = LoggerFactory.getLogger("ADMIN_LOGIN");
    private static final ObjectMapper mapper = new ObjectMapper();

    @Test
    void testLogProducesValidJson() throws Exception {
        log.info("login",
            kv("email", "admin@test.com"),
            kv("resultado", "EXITOSO"),
            kv("id", 1),
            kv("rol", "ADMIN"),
            kv("accion", "Inicio de sesion"),
            kv("ip", "127.0.0.1"),
            kv("userAgent", "JUnit")
        );

        Path logPath = Path.of("logs/admin-login.log");
        assertTrue(Files.exists(logPath), "Log file should exist");

        String content = Files.readString(logPath);
        assertFalse(content.isEmpty(), "Log file should not be empty");

        String lastLine = content.trim().lines().reduce((first, second) -> second).orElse("");
        assertTrue(lastLine.contains("admin@test.com"), "Should contain email");
        assertTrue(lastLine.contains("EXITOSO"), "Should contain resultado");

        @SuppressWarnings("unchecked")
        var json = mapper.readValue(lastLine, java.util.Map.class);
        assertEquals("admin@test.com", json.get("email"));
        assertEquals("EXITOSO", json.get("resultado"));
        assertEquals("ADMIN", json.get("rol"));
        assertEquals(1, json.get("id"));
    }

    @Test
    void testLogStructuredFields() throws Exception {
        log.info("register",
            kv("email", "newadmin@test.com"),
            kv("resultado", "FALLIDO"),
            kv("razon", "Ya existe un administrador"),
            kv("accion", "Registro de administrador"),
            kv("ip", "192.168.1.1"),
            kv("userAgent", "Chrome")
        );

        Path logPath = Path.of("logs/admin-login.log");
        String content = Files.readString(logPath);
        String lastLine = content.trim().lines().reduce((first, second) -> second).orElse("");

        @SuppressWarnings("unchecked")
        var json = mapper.readValue(lastLine, java.util.Map.class);
        assertEquals("newadmin@test.com", json.get("email"));
        assertEquals("FALLIDO", json.get("resultado"));
        assertEquals("Ya existe un administrador", json.get("razon"));
        assertEquals("192.168.1.1", json.get("ip"));
        assertEquals("Chrome", json.get("userAgent"));
    }
}
