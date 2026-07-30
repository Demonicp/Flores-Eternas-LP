package flores.eternas.backend.services;

import flores.eternas.backend.exception.ValidacionException;
import flores.eternas.backend.model.PasswordResetToken;
import flores.eternas.backend.model.Usuario;
import flores.eternas.backend.model.enums.Rol;
import flores.eternas.backend.repository.PasswordResetTokenRepository;
import flores.eternas.backend.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author esteban
 * Servicio de recuperacion de contrasena exclusivo para la cuenta
 * administradora del sistema. Orquesta la generacion del codigo de
 * un solo uso, su envio por correo, la validacion al intentar el
 * cambio y la actualizacion de la contrasena.
 *
 * Decisiones de diseno clave:
 * <ul>
 *   <li>El codigo se genera con SecureRandom y se persiste hasheado
 *       con BCrypt; el codigo plano solo viaja por email.</li>
 *   <li>El rate limit por correo vive en memoria (ConcurrentHashMap);
 *       se reinicia con el servidor, suficiente para este caso.</li>
 *   <li>{@code noRollbackFor = ValidacionException.class} garantiza
 *       que el incremento del contador de intentos y la invalidacion
 *       de tokens expirados persistan aunque se lance la excepcion.</li>
 *   <li>Ante un correo no registrado o de un no-admin, no se envia
 *       email y se devuelve 200 igual (anti-enumeracion).</li>
 * </ul>
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, noRollbackFor = ValidacionException.class)
public class PasswordResetService {

    private static final Logger adminLog = LoggerFactory.getLogger("ADMIN_LOGIN");

    private static final long EXPIRACION_MINUTOS = 10L;
    private static final long RATE_LIMIT_MS = 60_000L;
    private static final int MAX_INTENTOS = 5;
    private static final int LONGITUD_CODIGO = 6;

    private final PasswordResetTokenRepository tokenRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom random = new SecureRandom();
    private final ConcurrentHashMap<String, Long> ultimoRequestPorCorreo = new ConcurrentHashMap<>();

    public PasswordResetService(PasswordResetTokenRepository tokenRepository,
                                UsuarioRepository usuarioRepository,
                                EmailService emailService,
                                PasswordEncoder passwordEncoder) {
        this.tokenRepository = tokenRepository;
        this.usuarioRepository = usuarioRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * @author esteban
     * Procesa la solicitud de recuperacion: aplica rate limit por correo,
     * invalida el token activo anterior si existe, genera un codigo nuevo
     * hasheado y lo envia por email. Si el correo no corresponde a un
     * administrador, registra el intento y termina sin enviar nada.
     * @param correo Correo electronico capturado del request.
     * @throws ValidacionException Si el rate limit no se respeto.
     */
    public void solicitarCodigoRecuperacion(String correo) {
        aplicarRateLimit(correo);

        var usuarioOpt = usuarioRepository.findByCorreoElectronico(correo);

        if (usuarioOpt.isEmpty() || usuarioOpt.get().getRol() != Rol.ADMIN) {
            adminLog.warn("password_reset_request",
                    org.slf4j.event.Level.WARN,
                    "Intento de recuperacion para correo no-admin o inexistente: {}", correo);
            return;
        }

        tokenRepository.findFirstByCorreoAndUsadoFalseOrderByCreadoEnDesc(correo)
                .ifPresent(tokenAnterior -> {
                    tokenAnterior.setUsado(true);
                    tokenRepository.save(tokenAnterior);
                });

        String codigoPlano = generarCodigo();
        String codigoHash = passwordEncoder.encode(codigoPlano);

        PasswordResetToken token = new PasswordResetToken();
        token.setCorreo(correo);
        token.setCodigoHash(codigoHash);
        token.setExpiraEn(LocalDateTime.now().plusMinutes(EXPIRACION_MINUTOS));
        token.setUsado(false);
        token.setIntentos(0);
        token.setCreadoEn(LocalDateTime.now());
        tokenRepository.save(token);

        String html = EmailTemplates.codigoRecuperacion(codigoPlano);
        emailService.enviarEmail(correo, EmailTemplates.ASUNTO_RECUPERACION, html);

        adminLog.info("password_reset_request",
                "Codigo de recuperacion generado y enviado para admin {}", correo);
    }

    /**
     * @author esteban
     * Valida el codigo y, si todo es correcto, actualiza la contrasena
     * de la administradora y marca el token como consumido.
     * @param correo Correo electronico asociado al codigo.
     * @param codigoPlano Codigo de 6 digitos capturado del request.
     * @param nuevaContrasena Nueva contrasena en texto plano (ya validada
     *                        por Bean Validation en el controller).
     * @throws ValidacionException Si el codigo no existe, expiro, supero
     *         el maximo de intentos o no coincide.
     */
    public void validarCodigoYRestablecer(String correo, String codigoPlano, String nuevaContrasena) {
        PasswordResetToken token = tokenRepository
                .findFirstByCorreoAndUsadoFalseOrderByCreadoEnDesc(correo)
                .orElseThrow(() -> new ValidacionException("Código inválido o expirado"));

        if (token.getExpiraEn().isBefore(LocalDateTime.now())) {
            token.setUsado(true);
            tokenRepository.save(token);
            adminLog.warn("password_reset_verify",
                    "Intento de uso de codigo expirado para {}", correo);
            throw new ValidacionException("El código expiró, solicita uno nuevo");
        }

        if (token.getIntentos() >= MAX_INTENTOS) {
            token.setUsado(true);
            tokenRepository.save(token);
            adminLog.warn("password_reset_verify",
                    "Maximo de intentos alcanzado para {}", correo);
            throw new ValidacionException("Demasiados intentos fallidos, solicita un nuevo código");
        }

        if (!passwordEncoder.matches(codigoPlano, token.getCodigoHash())) {
            token.setIntentos(token.getIntentos() + 1);
            tokenRepository.save(token);
            adminLog.warn("password_reset_verify",
                    "Codigo incorrecto para {} (intento {}/{})",
                    correo, token.getIntentos(), MAX_INTENTOS);
            throw new ValidacionException("Código incorrecto");
        }

        Usuario usuario = usuarioRepository.findByCorreoElectronico(correo)
                .filter(u -> u.getRol() == Rol.ADMIN)
                .orElseThrow(() -> new ValidacionException("Usuario no encontrado"));

        usuario.setContrasena(passwordEncoder.encode(nuevaContrasena));
        usuarioRepository.save(usuario);

        token.setUsado(true);
        tokenRepository.save(token);

        adminLog.info("password_reset_success",
                "Contrasena actualizada exitosamente para admin {}", correo);
    }

    /**
     * @author esteban
     * Aplica la limitacion de un request por minuto por correo electronico.
     * Si el correo ya solicito un codigo dentro de la ventana, lanza
     * {@link ValidacionException}. La limitacion vive en memoria y se
     * reinicia con el servidor; no es distribuida ni persistente.
     * @param correo Correo sobre el que se controla la frecuencia.
     */
    private void aplicarRateLimit(String correo) {
        long ahora = System.currentTimeMillis();
        Long ultimo = ultimoRequestPorCorreo.get(correo);
        if (ultimo != null && (ahora - ultimo) < RATE_LIMIT_MS) {
            long segundosRestantes = (RATE_LIMIT_MS - (ahora - ultimo)) / 1000;
            throw new ValidacionException(
                    "Debes esperar " + segundosRestantes + " segundos antes de solicitar otro código");
        }
        ultimoRequestPorCorreo.put(correo, ahora);
    }

    /**
     * @author esteban
     * Genera un codigo numerico de 6 digitos usando SecureRandom.
     * Se garantiza el largo exacto con padding de ceros a la izquierda.
     * @return Codigo de 6 digitos como String (por ejemplo "048291").
     */
    private String generarCodigo() {
        int limiteSuperior = (int) Math.pow(10, LONGITUD_CODIGO);
        int numero = random.nextInt(limiteSuperior);
        return String.format("%0" + LONGITUD_CODIGO + "d", numero);
    }
}
