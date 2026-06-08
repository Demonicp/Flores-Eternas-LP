package flores.eternas.backend.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author esteban
 * Utilidad para la generacion y validacion de tokens JWT (JSON Web Token).
 * Se utiliza en el proceso de autenticacion para crear tokens seguros
 * que identifican al usuario logueado en cada peticion.
 * La clave secreta se obtiene de la variable de entorno JWT_SECRET.
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    private static final long EXPIRATION_TIME = 86400000; // 24 horas en milisegundos

    /**
     * @author esteban
     * Genera un token JWT con el ID, correo y rol del usuario.
     * @param id ID del usuario en la base de datos.
     * @param correo Correo electronico del usuario (se usa como subject).
     * @param rol Rol del usuario (ADMIN, EMPLEADO, CLIENTE).
     * @return Token JWT firmadocomo String.
     */
    public String generateToken(Long id, String correo, String rol) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        claims.put("rol", rol);
        return createToken(claims, correo);
    }

    /**
     * @author esteban
     * Crea el token JWT con los claims y subject especificados.
     * @param claims Mapa de datos adicionales a incluir en el token.
     * @param subject Correo del usuario (identificador unico).
     * @return Token JWT firmado.
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * @author esteban
     * Obtiene la clave secreta para firmar tokens desde la variable de entorno.
     * @return Clave SecretKey para HMAC-SHA.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * @author esteban
     * Extrae el ID del usuario del token.
     * @param token Token JWT.
     * @return ID del usuario.
     */
    public Long extractId(String token) {
        return extractClaim(token, claims -> claims.get("id", Long.class));
    }

    /**
     * @author esteban
     * Extrae el correo (subject) del token.
     * @param token Token JWT.
     * @return Correo del usuario.
     */
    public String extractCorreo(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * @author esteban
     * Extrae el rol del usuario del token.
     * @param token Token JWT.
     * @return Rol del usuario.
     */
    public String extractRol(String token) {
        return extractClaim(token, claims -> claims.get("rol", String.class));
    }

    /**
     * @author esteban
     * Extrae la fecha de expiracion del token.
     * @param token Token JWT.
     * @return Fecha de expiracion.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * @author esteban
     * Extrae un claim especifico del token.
     * @param token Token JWT.
     * @param claimsResolver Funcion para extraer el claim deseado.
     * @return Valor del claim solicitado.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * @author esteban
     * Extrae todos los claims del token.
     * @param token Token JWT.
     * @return Claims contenidos en el token.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * @author esteban
     * Verifica si el token ha expirado.
     * @param token Token JWT.
     * @return true si el token esta expirado, false si aun es valido.
     */
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * @author esteban
     * Valida un token JWT: verifica que no este expirado y que el subject sea correcto.
     * @param token Token JWT a validar.
     * @param correo Correo esperado en el token.
     * @return true si el token es valido, false si no lo es.
     */
    public Boolean validateToken(String token, String correo) {
        final String tokenCorreo = extractCorreo(token);
        return (tokenCorreo.equals(correo) && !isTokenExpired(token));
    }

    /**
     * @author esteban
     * Valida un token JWT sin verificar el subject.
     * @param token Token JWT a validar.
     * @return true si el token es valido y no esta expirado, false en caso contrario.
     */
    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}