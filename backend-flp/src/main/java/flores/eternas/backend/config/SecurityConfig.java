package flores.eternas.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

/**
 * @author esteban
 * Configuracion de seguridad de Spring Security.
 * Define las reglas de acceso a los endpoints de la API.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * @author esteban
     * Configura el encoder de contrasenas usando BCrypt.
     * Se utiliza para hashear y verificar contrasenas de usuarios.
     * @return PasswordEncoder configurado con BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

   
    configuration.addAllowedOriginPattern("https://*.vercel.app");

    configuration.setAllowedOrigins(List.of(
        "http://localhost:3000",
        "https://flores-eternas-lp.vercel.app" // si configuras un dominio fijo en Vercel
    ));

    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}

    /**
     * @author esteban
     * Configura la cadena de filtros de seguridad de Spring.
     * - CSRF deshabilitado (es API REST stateless).
     * - Endpoints de autenticacion publicos.
     * - Endpoints /api/admin/** requieren rol ADMIN.
     * - Sesiones stateless (JWT).
     * - Filtro JWT para autenticar peticiones.
     * @param http Configuracion HTTP de Spring Security.
     * @return SecurityFilterChain configurado.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> {})
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/flores/**").permitAll()
                        .requestMatchers("/api/pedidos/**").permitAll()
                        .requestMatchers("/api/catalogo/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categorias-ramo/**", "/api/colores-flor/**", "/api/ramos/**", "/api/tipos-flor/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
