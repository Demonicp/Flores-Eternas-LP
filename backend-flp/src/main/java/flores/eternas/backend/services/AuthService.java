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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author esteban
 * Servicio de autenticacion para el sistema Flores Eternas.
 * Maneja el registro de administradores y el login de usuarios.
 */
@Service
public class AuthService {

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

        return new LoginResponse(token, Rol.ADMIN.name(), request.getNombre());
    }

    /**
     * @author esteban
     * Autentica un usuario y genera un token JWT.
     * @param request Credenciales del usuario (correo y contrasena).
     * @return LoginResponse con el token JWT y datos del usuario.
     * @throws CredencialesInvalidasException si las credenciales son invalidas o el usuario no existe.
     */
    public LoginResponse login(LoginRequest request) {
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

        return new LoginResponse(token, usuario.getRol().name(), nombre);
    }
}