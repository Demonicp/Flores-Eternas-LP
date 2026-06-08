package flores.eternas.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author esteban
 * DTO para solicitar autenticacion (login) en el sistema.
 * Contiene las credenciales del usuario: correo y contrasena.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    /**
     * @author esteban
     * Correo electronico del usuario. Debe ser un email valido y no puede estar vacio.
     */
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Formato de correo inválido")
    private String correo;

    /**
     * @author esteban
     * Contrasena del usuario. No puede estar vacia.
     */
    @NotBlank(message = "La contraseña es obligatoria")
    private String contrasena;
}