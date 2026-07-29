package flores.eternas.backend.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import flores.eternas.backend.exception.ValidacionException;
import flores.eternas.backend.services.PasswordResetService;

/**
 * @author esteban
 * Tests de integracion del controller para los endpoints de recuperacion
 * de contrasena. Usa MockMvc para enviar requests HTTP reales y verificar
 * tanto el contrato del controller como la integracion con el
 * {@link GlobalExceptionHandler} para validaciones.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerPasswordResetTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PasswordResetService passwordResetService;

    private static final String FORGOT_PATH = "/api/auth/forgot-password";
    private static final String RESET_PATH = "/api/auth/reset-password";

    /* ─── POST /api/auth/forgot-password ─── */

    @Test
    void testForgotPassword_CorreoValido_Devuelve200ConMensajeGenerico() throws Exception {
        doNothing().when(passwordResetService).solicitarCodigoRecuperacion(anyString());

        mockMvc.perform(post(FORGOT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"admin@floreseternas.com\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", containsString("Si se encuentra registrado")));

        verify(passwordResetService).solicitarCodigoRecuperacion("admin@floreseternas.com");
    }

    @Test
    void testForgotPassword_CorreoVacio_Devuelve400ConErrorDeValidacion() throws Exception {
        mockMvc.perform(post(FORGOT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errors.correo").exists())
                .andExpect(jsonPath("$.errors.correo", containsString("obligatorio")));
    }

    @Test
    void testForgotPassword_CorreoInvalido_Devuelve400() throws Exception {
        mockMvc.perform(post(FORGOT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"esto-no-es-email\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.correo", containsString("Formato")));
    }

    @Test
    void testForgotPassword_CorreoFaltante_Devuelve400() throws Exception {
        mockMvc.perform(post(FORGOT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.correo").exists());
    }

    @Test
    void testForgotPassword_ServiceLanzaValidacion_Devuelve400() throws Exception {
        doThrow(new ValidacionException("Debes esperar 45 segundos antes de solicitar otro código"))
                .when(passwordResetService).solicitarCodigoRecuperacion(anyString());

        mockMvc.perform(post(FORGOT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"admin@floreseternas.com\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("esperar")));
    }

    /* ─── POST /api/auth/reset-password ─── */

    @Test
    void testResetPassword_BodyValido_Devuelve200ConMensajeExito() throws Exception {
        doNothing().when(passwordResetService)
                .validarCodigoYRestablecer(anyString(), anyString(), anyString());

        mockMvc.perform(post(RESET_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"admin@floreseternas.com\","
                                + "\"codigo\":\"482917\","
                                + "\"nuevaContrasena\":\"NuevaPass2024\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("actualizada")));

        verify(passwordResetService)
                .validarCodigoYRestablecer("admin@floreseternas.com", "482917", "NuevaPass2024");
    }

    @Test
    void testResetPassword_CodigoConLetras_Devuelve400() throws Exception {
        mockMvc.perform(post(RESET_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"admin@floreseternas.com\","
                                + "\"codigo\":\"abc123\","
                                + "\"nuevaContrasena\":\"NuevaPass2024\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.codigo").exists());
    }

    @Test
    void testResetPassword_CodigoCorto_Devuelve400() throws Exception {
        mockMvc.perform(post(RESET_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"admin@floreseternas.com\","
                                + "\"codigo\":\"12345\","
                                + "\"nuevaContrasena\":\"NuevaPass2024\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.codigo").exists());
    }

    @Test
    void testResetPassword_ContrasenaCorta_Devuelve400() throws Exception {
        mockMvc.perform(post(RESET_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"admin@floreseternas.com\","
                                + "\"codigo\":\"482917\","
                                + "\"nuevaContrasena\":\"corta1\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.nuevaContrasena").exists());
    }

    @Test
    void testResetPassword_ContrasenaSinNumeros_Devuelve400() throws Exception {
        mockMvc.perform(post(RESET_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"admin@floreseternas.com\","
                                + "\"codigo\":\"482917\","
                                + "\"nuevaContrasena\":\"SoloLetras\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.nuevaContrasena", containsString("letras y números")));
    }

    @Test
    void testResetPassword_ContrasenaSinLetras_Devuelve400() throws Exception {
        mockMvc.perform(post(RESET_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"admin@floreseternas.com\","
                                + "\"codigo\":\"482917\","
                                + "\"nuevaContrasena\":\"12345678\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.nuevaContrasena", containsString("letras y números")));
    }

    @Test
    void testResetPassword_ServiceLanzaValidacion_Devuelve400() throws Exception {
        doThrow(new ValidacionException("Código incorrecto"))
                .when(passwordResetService)
                .validarCodigoYRestablecer(anyString(), anyString(), anyString());

        mockMvc.perform(post(RESET_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"admin@floreseternas.com\","
                                + "\"codigo\":\"000000\","
                                + "\"nuevaContrasena\":\"NuevaPass2024\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Código incorrecto")));
    }

    @Test
    void testResetPassword_BodyVacio_Devuelve400() throws Exception {
        mockMvc.perform(post(RESET_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.correo").exists())
                .andExpect(jsonPath("$.errors.codigo").exists())
                .andExpect(jsonPath("$.errors.nuevaContrasena").exists());
    }
}
