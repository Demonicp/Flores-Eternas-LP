package flores.eternas.backend.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Map;

//Santiago Montenegro HU6
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<Map<String, String>> handleCredencialesInvalidas(CredencialesInvalidasException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(ValidacionException.class)
    public ResponseEntity<Map<String, String>> handleValidacion(ValidacionException ex) {
        return ResponseEntity.badRequest()
                .body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleIntegrityViolation(DataIntegrityViolationException ex) {
        String message = "No se puede eliminar porque este registro está siendo usado en otra parte del sistema.";
        if (ex.getRootCause() != null) {
            String rootMsg = ex.getRootCause().getMessage();
            if (rootMsg != null) {
                if (rootMsg.toLowerCase().contains("detalle_ramo")) {
                    message = "No se puede eliminar: este tipo de flor está asignado a uno o más ramos.";
                } else if (rootMsg.toLowerCase().contains("ramo")) {
                    message = "No se puede eliminar: esta categoría está asignada a uno o más ramos.";
                }
            }
        }
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", message));
    }

    /**
     * @author esteban
     * Maneja los errores de validacion de Bean Validation lanzados por
     * {@code @Valid} en los DTOs de request. Devuelve 400 Bad Request con
     * un mensaje legible y un mapa field -> mensaje para que el frontend
     * pueda mostrar errores por campo si lo desea.
     * @param ex Excepcion con los errores de validacion acumulados.
     * @return ResponseEntity con status 400, mensaje consolidado y detalle
     *         de errores por campo.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errores.put(error.getField(), error.getDefaultMessage());
        }

        String message = errores.isEmpty()
                ? "Datos de entrada inválidos"
                : String.join(", ", errores.values());

        Map<String, Object> body = new HashMap<>();
        body.put("message", message);
        body.put("errors", errores);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Ocurrió un error inesperado: " + ex.getMessage()));
    }
}
