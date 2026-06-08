package flores.eternas.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author esteban
 * Controlador REST para endpoints protegidos del administrador.
 * Solo usuarios con rol ADMIN pueden acceder a estos endpoints.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    /**
     * @author esteban
     * Endpoint de prueba para verificar el acceso de administrador.
     * Requiere rol ADMIN para ser accederdo.
     * @return ResponseEntity con un mensaje de confirmacion.
     */
    @GetMapping("/test")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> testAdmin() {
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Acceso concedido como administrador");
        return ResponseEntity.ok(response);
    }
}