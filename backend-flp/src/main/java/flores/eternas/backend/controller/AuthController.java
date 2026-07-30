package flores.eternas.backend.controller;

import flores.eternas.backend.dto.ForgotPasswordRequest;
import flores.eternas.backend.dto.LoginRequest;
import flores.eternas.backend.dto.LoginResponse;
import flores.eternas.backend.dto.RegisterRequest;
import flores.eternas.backend.dto.ResetPasswordRequest;
import flores.eternas.backend.services.AuthService;
import flores.eternas.backend.services.PasswordResetService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author esteban
 * Controlador REST para la autenticacion de usuarios.
 * Proporciona endpoints para el registro de administradores, el login
 * y la recuperacion de contrasena de la cuenta administradora.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    public AuthController(AuthService authService, PasswordResetService passwordResetService) {
        this.authService = authService;
        this.passwordResetService = passwordResetService;
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

    /**
     * @author esteban
     * Endpoint para solicitar el envio de un codigo de recuperacion de
     * contrasena. Siempre responde 200 con un mensaje generico para
     * evitar que un atacante pueda enumerar correos registrados.
     * @param request Correo electronico capturado del formulario.
     * @return ResponseEntity con el mensaje generico de confirmacion.
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.solicitarCodigoRecuperacion(request.getCorreo());
        return ResponseEntity.ok(Map.of(
                "message",
                "Si se encuentra registrado, le enviaremos un correo con el código de recuperación."
        ));
    }

    /**
     * @author esteban
     * Endpoint para restablecer la contrasena a partir del codigo de 6
     * digitos recibido por correo. Valida el codigo, su vigencia y la
     * politica de contrasena antes de persistir el cambio.
     * @param request Correo, codigo de 6 digitos y nueva contrasena.
     * @return ResponseEntity con el mensaje de exito.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.validarCodigoYRestablecer(
                request.getCorreo(),
                request.getCodigo(),
                request.getNuevaContrasena()
        );
        return ResponseEntity.ok(Map.of("message", "Contraseña actualizada exitosamente."));
    }
}
