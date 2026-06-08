package flores.eternas.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author esteban
 * DTO con la respuesta de autenticacion exitosa.
 * Contiene el token JWT y datos basicos del usuario autenticado.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    /**
     * @author esteban
     * Token JWT generado tras la autenticacion exitosa.
     */
    private String token;

    /**
     * @author esteban
     * Rol del usuario autenticado (ADMIN, EMPLEADO, CLIENTE).
     */
    private String rol;

    /**
     * @author esteban
     * Nombre completo del usuario obtenido de la entidad Persona asociada.
     */
    private String nombre;
}