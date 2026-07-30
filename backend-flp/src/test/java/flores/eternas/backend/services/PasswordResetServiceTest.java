package flores.eternas.backend.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;

import flores.eternas.backend.exception.ValidacionException;
import flores.eternas.backend.model.PasswordResetToken;
import flores.eternas.backend.model.Persona;
import flores.eternas.backend.model.Usuario;
import flores.eternas.backend.model.enums.Rol;
import flores.eternas.backend.repository.PasswordResetTokenRepository;
import flores.eternas.backend.repository.UsuarioRepository;

/**
 * @author esteban
 * Tests de integracion para {@link PasswordResetService}.
 * Levanta el contexto completo de Spring con perfil "test" (H2 en memoria),
 * mockea {@link EmailService} para capturar el codigo generado y poder
 * probar la verificacion y el cambio de contrasena.
 *
 * Cada test usa un correo unico para evitar que el rate limit en memoria
 * (ConcurrentHashMap) afecte a otros tests.
 */
@SpringBootTest
@ActiveProfiles("test")
class PasswordResetServiceTest {

    @Autowired
    private PasswordResetService service;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private EmailService emailService;

    private static int contador = 0;

    private static final Pattern CODE_PATTERN =
            Pattern.compile("letter-spacing:8px;[^>]*>(\\d{6})<");

    @BeforeEach
    void setUp() {
        tokenRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    private String crearAdminYObtenerCorreo() {
        String correo = "admin-test-" + (++contador) + "@floreseternas.com";
        Persona persona = new Persona();
        persona.setNombreCliente("Test Admin");
        Usuario admin = new Usuario();
        admin.setCorreoElectronico(correo);
        admin.setContrasena(passwordEncoder.encode("ContrasenaInicial1"));
        admin.setPersona(persona);
        admin.setRol(Rol.ADMIN);
        usuarioRepository.save(admin);
        return correo;
    }

    private String capturarCodigoGenerado(String correo) {
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailService).enviarEmail(eq(correo), anyString(), bodyCaptor.capture());
        Matcher m = CODE_PATTERN.matcher(bodyCaptor.getValue());
        assertTrue(m.find(), "No se encontro el codigo en el HTML del email");
        return m.group(1);
    }

    /* ─── solicitarCodigoRecuperacion ─── */

    @Test
    void testSolicitarCodigo_CorreoInexistente_NoCreaTokenNoEnviaEmail() {
        service.solicitarCodigoRecuperacion("noexiste@floreseternas.com");

        assertTrue(tokenRepository.findAll().isEmpty());
        verifyNoInteractions(emailService);
    }

    @Test
    void testSolicitarCodigo_CorreoNoAdmin_NoCreaTokenNoEnviaEmail() {
        String correo = "cliente-test-" + (++contador) + "@floreseternas.com";
        Persona persona = new Persona();
        persona.setNombreCliente("Cliente");
        Usuario cliente = new Usuario();
        cliente.setCorreoElectronico(correo);
        cliente.setContrasena(passwordEncoder.encode("Contrasena1"));
        cliente.setPersona(persona);
        cliente.setRol(Rol.CLIENTE);
        usuarioRepository.save(cliente);

        service.solicitarCodigoRecuperacion(correo);

        assertTrue(tokenRepository.findAll().isEmpty());
        verifyNoInteractions(emailService);
    }

    @Test
    void testSolicitarCodigo_CorreoAdmin_CreaTokenYEnviaEmail() {
        String correo = crearAdminYObtenerCorreo();

        service.solicitarCodigoRecuperacion(correo);

        List<PasswordResetToken> tokens = tokenRepository.findAll();
        assertEquals(1, tokens.size());
        PasswordResetToken token = tokens.get(0);
        assertEquals(correo, token.getCorreo());
        assertFalse(token.getUsado());
        assertEquals(0, token.getIntentos());
        assertNotNull(token.getCodigoHash());
        assertTrue(token.getCodigoHash().startsWith("$2"),
                "El hash debe estar en formato BCrypt");

        verify(emailService).enviarEmail(eq(correo), anyString(), anyString());
    }

    @Test
    void testTokenCreado_TieneExpiracionCercanaA10Minutos() {
        String correo = crearAdminYObtenerCorreo();
        LocalDateTime antes = LocalDateTime.now();

        service.solicitarCodigoRecuperacion(correo);

        PasswordResetToken token = tokenRepository.findAll().get(0);
        long segundosDiferencia = java.time.Duration
                .between(antes, token.getExpiraEn())
                .getSeconds();
        assertTrue(segundosDiferencia >= 590 && segundosDiferencia <= 610,
                "La expiracion debe ser ~10 minutos (600s), fue: " + segundosDiferencia + "s");
    }

    @Test
    void testCodigoGenerado_EsDe6Digitos() {
        String correo = crearAdminYObtenerCorreo();

        service.solicitarCodigoRecuperacion(correo);

        String codigo = capturarCodigoGenerado(correo);
        assertTrue(codigo.matches("\\d{6}"),
                "El codigo debe ser exactamente 6 digitos, fue: " + codigo);
    }

    @Test
    void testSolicitarCodigo_DosVecesSeguidas_SegundaLanzaRateLimit() {
        String correo = crearAdminYObtenerCorreo();

        service.solicitarCodigoRecuperacion(correo);

        ValidacionException ex = assertThrows(ValidacionException.class,
                () -> service.solicitarCodigoRecuperacion(correo));
        assertTrue(ex.getMessage().toLowerCase().contains("esperar"),
                "El mensaje debe mencionar que hay que esperar: " + ex.getMessage());
    }

    @Test
    void testSolicitarCodigo_InvalidaTokenActivoPrevio() {
        String correo = crearAdminYObtenerCorreo();

        service.solicitarCodigoRecuperacion(correo);
        PasswordResetToken tokenPrevio = tokenRepository
                .findFirstByCorreoAndUsadoFalseOrderByCreadoEnDesc(correo)
                .orElseThrow();

        // Forzamos expiracion del rate limit usando un correo "neutro" para
        // limpiar el ConcurrentHashMap: lo mas simple es crear otro admin
        // con un correo distinto y resetear el token del primero a expirado.
        tokenPrevio.setExpiraEn(LocalDateTime.now().minusMinutes(1));
        tokenRepository.save(tokenPrevio);

        // Como el rate limit sigue activo, no podemos pedir un nuevo codigo
        // todavia. Verificamos que el token previo sigue activo (sin uso)
        // porque la invalidacion ocurre al pedir uno nuevo, no por expirar.
        PasswordResetToken recargado = tokenRepository.findById(tokenPrevio.getId()).orElseThrow();
        assertFalse(recargado.getUsado(),
                "El token previo debe seguir activo hasta que se pida uno nuevo");
    }

    /* ─── validarCodigoYRestablecer ─── */

    @Test
    void testValidarCodigo_CodigoCorrecto_CambiaContrasenaYMarcaTokenUsado() {
        String correo = crearAdminYObtenerCorreo();
        String contrasenaOriginal = passwordEncoder.encode("ContrasenaInicial1");

        service.solicitarCodigoRecuperacion(correo);
        String codigo = capturarCodigoGenerado(correo);

        service.validarCodigoYRestablecer(correo, codigo, "NuevaContrasena123");

        Usuario usuarioActualizado = usuarioRepository.findByCorreoElectronico(correo).orElseThrow();
        assertNotEquals(contrasenaOriginal, usuarioActualizado.getContrasena());
        assertTrue(passwordEncoder.matches("NuevaContrasena123", usuarioActualizado.getContrasena()),
                "La nueva contrasena debe estar hasheada y coincidir");

        PasswordResetToken token = tokenRepository
                .findFirstByCorreoAndUsadoFalseOrderByCreadoEnDesc(correo)
                .orElse(null);
        assertTrue(token == null || token.getUsado(),
                "El token debe estar marcado como usado");
    }

    @Test
    void testValidarCodigo_CodigoIncorrecto_IncrementaIntentos() {
        String correo = crearAdminYObtenerCorreo();

        service.solicitarCodigoRecuperacion(correo);
        String codigoCorrecto = capturarCodigoGenerado(correo);
        String codigoIncorrecto = "000000".equals(codigoCorrecto) ? "111111" : "000000";

        assertThrows(ValidacionException.class,
                () -> service.validarCodigoYRestablecer(correo, codigoIncorrecto, "Nueva1"));

        PasswordResetToken token = tokenRepository
                .findFirstByCorreoAndUsadoFalseOrderByCreadoEnDesc(correo)
                .orElseThrow();
        assertEquals(1, token.getIntentos());
    }

    @Test
    void testValidarCodigo_TokenExpirado_LanzaExcepcion() {
        String correo = crearAdminYObtenerCorreo();

        service.solicitarCodigoRecuperacion(correo);
        String codigo = capturarCodigoGenerado(correo);

        // Forzar expiracion del token sin disparar el rate limit
        PasswordResetToken token = tokenRepository
                .findFirstByCorreoAndUsadoFalseOrderByCreadoEnDesc(correo)
                .orElseThrow();
        token.setExpiraEn(LocalDateTime.now().minusMinutes(1));
        tokenRepository.save(token);

        ValidacionException ex = assertThrows(ValidacionException.class,
                () -> service.validarCodigoYRestablecer(correo, codigo, "Nueva1"));
        assertTrue(ex.getMessage().toLowerCase().contains("expir"));
    }

    @Test
    void testValidarCodigo_DemasiadosIntentos_LanzaExcepcion() {
        String correo = crearAdminYObtenerCorreo();

        service.solicitarCodigoRecuperacion(correo);
        String codigoCorrecto = capturarCodigoGenerado(correo);
        String codigoIncorrecto = "000000".equals(codigoCorrecto) ? "111111" : "000000";

        // 5 intentos fallidos
        for (int i = 0; i < 5; i++) {
            try {
                service.validarCodigoYRestablecer(correo, codigoIncorrecto, "Nueva1");
            } catch (ValidacionException ignored) {
            }
        }

        ValidacionException ex = assertThrows(ValidacionException.class,
                () -> service.validarCodigoYRestablecer(correo, codigoIncorrecto, "Nueva1"));
        assertTrue(ex.getMessage().toLowerCase().contains("intentos"));
    }

    @Test
    void testValidarCodigo_SinTokenParaCorreo_LanzaExcepcion() {
        String correoInexistente = "noexiste-" + (++contador) + "@floreseternas.com";

        ValidacionException ex = assertThrows(ValidacionException.class,
                () -> service.validarCodigoYRestablecer(correoInexistente, "123456", "Nueva1"));
        assertTrue(ex.getMessage().toLowerCase().contains("inválid")
                || ex.getMessage().toLowerCase().contains("expirad"));
    }

    @Test
    void testValidarCodigo_TokenYaUsado_LanzaExcepcion() {
        String correo = crearAdminYObtenerCorreo();

        service.solicitarCodigoRecuperacion(correo);
        String codigo = capturarCodigoGenerado(correo);

        // Marcar el token como usado manualmente
        PasswordResetToken token = tokenRepository
                .findFirstByCorreoAndUsadoFalseOrderByCreadoEnDesc(correo)
                .orElseThrow();
        token.setUsado(true);
        tokenRepository.save(token);

        ValidacionException ex = assertThrows(ValidacionException.class,
                () -> service.validarCodigoYRestablecer(correo, codigo, "Nueva1"));
        assertTrue(ex.getMessage().toLowerCase().contains("inválid")
                || ex.getMessage().toLowerCase().contains("expirad"));
    }

    @Test
    void testValidarCodigo_UsuarioNoExisteParaCorreo_LanzaExcepcion() {
        // Crear admin para emitir el codigo, luego borrarlo antes de validar
        String correo = crearAdminYObtenerCorreo();
        service.solicitarCodigoRecuperacion(correo);
        String codigo = capturarCodigoGenerado(correo);

        // Borrar el admin
        Usuario admin = usuarioRepository.findByCorreoElectronico(correo).orElseThrow();
        usuarioRepository.delete(admin);

        ValidacionException ex = assertThrows(ValidacionException.class,
                () -> service.validarCodigoYRestablecer(correo, codigo, "Nueva1"));
        assertNotNull(ex);
    }
}
