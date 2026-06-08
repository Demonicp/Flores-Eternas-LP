package flores.eternas.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * @author esteban
 * Manejador global de excepciones para la API.
 * Retorna respuestas JSON consistentes para todos los errores.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * @author esteban
     * Maneja excepciones de credenciales invalidas (login fallido).
     * @param ex Excepcion con mensaje de error.
     * @return ResponseEntity con error 401.
     */
    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<Map<String, String>> handleCredencialesInvalidas(CredencialesInvalidasException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * @author esteban
     * Maneja excepciones de registro cerrado (ya existe admin).
     * @param ex Excepcion con mensaje de error.
     * @return ResponseEntity con error 409.
     */
    @ExceptionHandler(RegistroCerradoException.class)
    public ResponseEntity<Map<String, String>> handleRegistroCerrado(RegistroCerradoException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * @author esteban
     * Maneja excepciones de acceso denegado (sin permisos).
     * @param ex Excepcion de acceso denegado.
     * @return ResponseEntity con error 403.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDenied(AccessDeniedException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Acceso denegado");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    /**
     * @author esteban
     * Maneja errores de validacion de DTOs.
     * @param ex Excepcion con errores de validacion.
     * @return ResponseEntity con error 400 y detalles de validacion.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errores = new HashMap<>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errores.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        response.put("error", "Error de validacion");
        response.put("detalles", errores);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * @author esteban
     * Maneja excepciones generales del sistema.
     * @param ex Excepcion general.
     * @return ResponseEntity con error 500 sin detalles (seguridad).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Error interno del servidor");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}