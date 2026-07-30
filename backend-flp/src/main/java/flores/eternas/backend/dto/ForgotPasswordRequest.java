package flores.eternas.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author esteban
 * DTO para solicitar el envio de un codigo de recuperacion de contrasena.
 * Contiene unicamente el correo electronico de la administradora.
 * La respuesta del backend es siempre la misma exista o no el correo,
 * para evitar filtrar informacion sobre correos registrados.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordRequest {

    /**
     * @author esteban
     * Correo electronico de la administradora que olvido su contrasena.
     */
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Formato de correo inválido")
    private String correo;
}
