package flores.eternas.backend.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * @author esteban
 * Tests unitarios de las anotaciones de Bean Validation declaradas
 * en los DTOs del feature de recuperacion de contrasena
 * ({@link ForgotPasswordRequest} y {@link ResetPasswordRequest}).
 * No levanta contexto de Spring: usa el Validator estandar de Jakarta.
 */
class DtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    /* ─── ForgotPasswordRequest ─── */

    @Test
    void testForgotPassword_CorreoValido_NoViolations() {
        ForgotPasswordRequest req = new ForgotPasswordRequest("admin@floreseternas.com");
        Set<ConstraintViolation<ForgotPasswordRequest>> violations = validator.validate(req);
        assertTrue(violations.isEmpty(),
                "Un correo valido no debe generar violaciones");
    }

    @Test
    void testForgotPassword_CorreoVacio_LanzaNotBlank() {
        ForgotPasswordRequest req = new ForgotPasswordRequest("");
        Set<ConstraintViolation<ForgotPasswordRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
        assertTrue(containsMessage(violations, "correo", "El correo es obligatorio"));
    }

    @Test
    void testForgotPassword_CorreoNulo_LanzaNotBlank() {
        ForgotPasswordRequest req = new ForgotPasswordRequest(null);
        Set<ConstraintViolation<ForgotPasswordRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
        assertTrue(containsMessage(violations, "correo", "El correo es obligatorio"));
    }

    @Test
    void testForgotPassword_CorreoInvalido_LanzaEmail() {
        ForgotPasswordRequest req = new ForgotPasswordRequest("esto-no-es-email");
        Set<ConstraintViolation<ForgotPasswordRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
        assertTrue(containsMessage(violations, "correo", "Formato de correo inválido"));
    }

    /* ─── ResetPasswordRequest ─── */

    @Test
    void testResetPassword_RequestCompletoValido_NoViolations() {
        ResetPasswordRequest req = new ResetPasswordRequest(
                "admin@floreseternas.com",
                "482917",
                "Floreseternas123");
        Set<ConstraintViolation<ResetPasswordRequest>> violations = validator.validate(req);
        assertTrue(violations.isEmpty(),
                "Un request completo y valido no debe generar violaciones");
    }

    @Test
    void testResetPassword_CorreoVacio_LanzaNotBlank() {
        ResetPasswordRequest req = new ResetPasswordRequest("", "482917", "Floreseternas123");
        Set<ConstraintViolation<ResetPasswordRequest>> violations = validator.validate(req);
        assertTrue(containsMessage(violations, "correo", "El correo es obligatorio"));
    }

    @Test
    void testResetPassword_CorreoInvalido_LanzaEmail() {
        ResetPasswordRequest req = new ResetPasswordRequest("no-es-email", "482917", "Floreseternas123");
        Set<ConstraintViolation<ResetPasswordRequest>> violations = validator.validate(req);
        assertTrue(containsMessage(violations, "correo", "Formato de correo inválido"));
    }

    @Test
    void testResetPassword_CodigoVacio_LanzaNotBlank() {
        ResetPasswordRequest req = new ResetPasswordRequest("admin@floreseternas.com", "", "Floreseternas123");
        Set<ConstraintViolation<ResetPasswordRequest>> violations = validator.validate(req);
        assertTrue(containsMessage(violations, "codigo", "El código es obligatorio"));
    }

    @Test
    void testResetPassword_CodigoNulo_LanzaNotBlank() {
        ResetPasswordRequest req = new ResetPasswordRequest("admin@floreseternas.com", null, "Floreseternas123");
        Set<ConstraintViolation<ResetPasswordRequest>> violations = validator.validate(req);
        assertTrue(containsMessage(violations, "codigo", "El código es obligatorio"));
    }

    @Test
    void testResetPassword_Codigo5Digitos_LanzaSize() {
        ResetPasswordRequest req = new ResetPasswordRequest("admin@floreseternas.com", "12345", "Floreseternas123");
        Set<ConstraintViolation<ResetPasswordRequest>> violations = validator.validate(req);
        assertTrue(containsMessage(violations, "codigo", "El código debe tener 6 dígitos"));
    }

    @Test
    void testResetPassword_Codigo7Digitos_LanzaSize() {
        ResetPasswordRequest req = new ResetPasswordRequest("admin@floreseternas.com", "1234567", "Floreseternas123");
        Set<ConstraintViolation<ResetPasswordRequest>> violations = validator.validate(req);
        assertTrue(containsMessage(violations, "codigo", "El código debe tener 6 dígitos"));
    }

    @Test
    void testResetPassword_CodigoConLetras_LanzaPattern() {
        ResetPasswordRequest req = new ResetPasswordRequest("admin@floreseternas.com", "12345a", "Floreseternas123");
        Set<ConstraintViolation<ResetPasswordRequest>> violations = validator.validate(req);
        assertTrue(containsMessage(violations, "codigo", "El código debe contener solo números"));
    }

    @Test
    void testResetPassword_ContrasenaVacia_LanzaNotBlank() {
        ResetPasswordRequest req = new ResetPasswordRequest("admin@floreseternas.com", "482917", "");
        Set<ConstraintViolation<ResetPasswordRequest>> violations = validator.validate(req);
        assertTrue(containsMessage(violations, "nuevaContrasena", "La nueva contraseña es obligatoria"));
    }

    @Test
    void testResetPassword_ContrasenaCorta_LanzaSize() {
        ResetPasswordRequest req = new ResetPasswordRequest("admin@floreseternas.com", "482917", "Ab1");
        Set<ConstraintViolation<ResetPasswordRequest>> violations = validator.validate(req);
        assertTrue(containsMessage(violations, "nuevaContrasena",
                "La contraseña debe tener al menos 8 caracteres"));
    }

    @Test
    void testResetPassword_ContrasenaSoloLetras_LanzaPattern() {
        ResetPasswordRequest req = new ResetPasswordRequest(
                "admin@floreseternas.com", "482917", "SoloLetras");
        Set<ConstraintViolation<ResetPasswordRequest>> violations = validator.validate(req);
        assertTrue(containsMessage(violations, "nuevaContrasena",
                "La contraseña debe mezclar letras y números"));
    }

    @Test
    void testResetPassword_ContrasenaSoloNumeros_LanzaPattern() {
        ResetPasswordRequest req = new ResetPasswordRequest(
                "admin@floreseternas.com", "482917", "12345678");
        Set<ConstraintViolation<ResetPasswordRequest>> violations = validator.validate(req);
        assertTrue(containsMessage(violations, "nuevaContrasena",
                "La contraseña debe mezclar letras y números"));
    }

    @Test
    void testResetPassword_VariosCamposInvalidos_ReportaTodos() {
        ResetPasswordRequest req = new ResetPasswordRequest("no-es-email", "abc", "corta");
        Set<ConstraintViolation<ResetPasswordRequest>> violations = validator.validate(req);
        assertFalse(getMessages(violations, "correo").isEmpty());
        assertFalse(getMessages(violations, "codigo").isEmpty());
        assertFalse(getMessages(violations, "nuevaContrasena").isEmpty());
    }

    /* ─── Helpers ─── */

    /**
     * Devuelve todos los mensajes de violacion asociados a un campo.
     * @return lista (posiblemente vacia) de mensajes para el campo indicado.
     */
    private static <T> List<String> getMessages(Set<ConstraintViolation<T>> violations, String field) {
        return violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals(field))
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
    }

    /**
     * Verifica si entre las violaciones de un campo existe una con el mensaje exacto.
     * Util cuando varios constraints pueden fallar simultaneamente y el orden
     * devuelto por el Validator no esta garantizado.
     */
    private static <T> boolean containsMessage(Set<ConstraintViolation<T>> violations,
                                              String field,
                                              String expectedMessage) {
        return getMessages(violations, field).contains(expectedMessage);
    }
}
