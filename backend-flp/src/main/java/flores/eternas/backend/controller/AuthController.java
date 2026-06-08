package flores.eternas.backend.controller;

import flores.eternas.backend.dto.LoginRequest;
import flores.eternas.backend.dto.LoginResponse;
import flores.eternas.backend.dto.RegisterRequest;
import flores.eternas.backend.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author esteban
 * Controlador REST para la autenticacion de usuarios.
 * Proporciona endpoints para el registro de administradores y el login.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * @author esteban
     * Endpoint para registrar el primer administrador del sistema.
     * Solo funciona cuando no existe ningun otro administrador registrado.
     * @param request Datos del administrador (correo, contrasena, nombre).
     * @return ResponseEntity con el token JWT si el registro es exitoso.
     */
    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        LoginResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    /**
     * @author esteban
     * Endpoint para iniciar sesion y obtener un token JWT.
     * @param request Credenciales del usuario (correo y contrasena).
     * @return ResponseEntity con el token JWT si las credenciales son validas.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}