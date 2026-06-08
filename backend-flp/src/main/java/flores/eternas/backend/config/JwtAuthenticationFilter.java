package flores.eternas.backend.config;

import flores.eternas.backend.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * @author esteban
 * Filtro de autenticacion JWT que intercepta cada peticion HTTP.
 * Extrae el token JWT del header Authorization, lo valida y establece
 * la autenticacion en el contexto de Spring Security.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * @author esteban
     * Metodo que se ejecuta en cada peticion HTTP.
     * Si encuentra un token JWT valido, autentica al usuario.
     * @param request Peticion HTTP entrante.
     * @param response Respuesta HTTP.
     * @param filterChain Cadena de filtros de Spring.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            if (jwtUtil.validateToken(token)) {
                Long id = jwtUtil.extractId(token);
                String correo = jwtUtil.extractCorreo(token);
                String rol = jwtUtil.extractRol(token);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                correo,
                                null,
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + rol))
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}