package flores.eternas.backend.services;

import flores.eternas.backend.dto.LoginRequest;
import flores.eternas.backend.dto.LoginResponse;
import flores.eternas.backend.dto.RegisterRequest;
import flores.eternas.backend.exception.CredencialesInvalidasException;
import flores.eternas.backend.exception.RegistroCerradoException;
import flores.eternas.backend.model.Persona;
import flores.eternas.backend.model.Usuario;
import flores.eternas.backend.model.enums.Rol;
import flores.eternas.backend.repository.UsuarioRepository;
import flores.eternas.backend.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static net.logstash.logback.argument.StructuredArguments.kv;

/**
 * @author esteban
 * Servicio de autenticacion para el sistema Flores Eternas.
 * Maneja el registro de administradores y el login de usuarios.
 */
@Service
public class AuthService {

    private static final Logger adminLog = LoggerFactory.getLogger("ADMIN_LOGIN");

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * @author esteban
     * Registra el primer administrador del sistema.
     * Solo funciona cuando no existe ningun otro administrador en la base de datos.
     * @param request Datos del administrador a registrar.
     * @return LoginResponse con el token JWT del nuevo administrador.
     * @throws RegistroCerradoException si ya existe un administrador registrado.
     */
    @Transactional
    public LoginResponse register(RegisterRequest request) {
        adminLog.info("register",
            kv("resultado", "INTENTO"),
            kv("email", request.getCorreo()),
            kv("accion", "Registro de administrador"),
            kv("ip", getClientIp()),
            kv("userAgent", getUserAgent())
        );

        try {
            if (usuarioRepository.existsByRol(Rol.ADMIN)) {
                throw new RegistroCerradoException("Ya existe un administrador registrado en el sistema");
            }

            if (usuarioRepository.existsByCorreoElectronico(request.getCorreo())) {
                throw new CredencialesInvalidasException("El correo electronico ya esta registrado");
            }

            Persona persona = new Persona();
            persona.setNombreCliente(request.getNombre());
            persona.setTelefono(null);
            persona.setCedula(null);
            persona.setFechaNacimiento(null);

            Usuario usuario = new Usuario();
            usuario.setCorreoElectronico(request.getCorreo());
            usuario.setContrasena(passwordEncoder.encode(request.getContrasena()));
            usuario.setPersona(persona);
            usuario.setRol(Rol.ADMIN);

            Usuario usuarioGuardado = usuarioRepository.save(usuario);

            String token = jwtUtil.generateToken(
                    usuarioGuardado.getId(),
                    usuarioGuardado.getCorreoElectronico(),
                    usuarioGuardado.getRol().name()
            );

            adminLog.info("register",
                kv("resultado", "EXITOSO"),
                kv("email", request.getCorreo()),
                kv("id", usuarioGuardado.getId()),
                kv("rol", usuarioGuardado.getRol().name()),
                kv("accion", "Registro de administrador"),
                kv("ip", getClientIp()),
                kv("userAgent", getUserAgent())
            );

            return new LoginResponse(token, Rol.ADMIN.name(), request.getNombre());
        } catch (RegistroCerradoException | CredencialesInvalidasException e) {
            adminLog.warn("register",
                kv("resultado", "FALLIDO"),
                kv("email", request.getCorreo()),
                kv("razon", e.getMessage()),
                kv("accion", "Registro de administrador"),
                kv("ip", getClientIp()),
                kv("userAgent", getUserAgent())
            );
            throw e;
        }
    }

    /**
     * @author esteban
     * Autentica un usuario y genera un token JWT.
     * @param request Credenciales del usuario (correo y contrasena).
     * @return LoginResponse con el token JWT y datos del usuario.
     * @throws CredencialesInvalidasException si las credenciales son invalidas o el usuario no existe.
     */
    public LoginResponse login(LoginRequest request) {
        adminLog.info("login",
            kv("resultado", "INTENTO"),
            kv("email", request.getCorreo()),
            kv("accion", "Inicio de sesion"),
            kv("ip", getClientIp()),
            kv("userAgent", getUserAgent())
        );

        try {
            Usuario usuario = usuarioRepository.findByCorreoElectronico(request.getCorreo())
                    .orElseThrow(() -> new CredencialesInvalidasException("Credenciales invalidas"));

            if (!passwordEncoder.matches(request.getContrasena(), usuario.getContrasena())) {
                throw new CredencialesInvalidasException("Credenciales invalidas");
            }

            String token = jwtUtil.generateToken(
                    usuario.getId(),
                    usuario.getCorreoElectronico(),
                    usuario.getRol().name()
            );

            String nombre = usuario.getPersona() != null ? usuario.getPersona().getNombreCliente() : "";

            adminLog.info("login",
                kv("resultado", "EXITOSO"),
                kv("email", request.getCorreo()),
                kv("id", usuario.getId()),
                kv("rol", usuario.getRol().name()),
                kv("accion", "Inicio de sesion"),
                kv("ip", getClientIp()),
                kv("userAgent", getUserAgent())
            );

            return new LoginResponse(token, usuario.getRol().name(), nombre);
        } catch (CredencialesInvalidasException e) {
            adminLog.warn("login",
                kv("resultado", "FALLIDO"),
                kv("email", request.getCorreo()),
                kv("razon", e.getMessage()),
                kv("accion", "Inicio de sesion"),
                kv("ip", getClientIp()),
                kv("userAgent", getUserAgent())
            );
            throw e;
        }
    }

    private String getClientIp() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) return "unknown";
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private String getUserAgent() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) return "unknown";
        String ua = request.getHeader("User-Agent");
        return ua != null ? ua : "unknown";
    }

    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs != null ? attrs.getRequest() : null;
    }
}
