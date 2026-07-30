package flores.eternas.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author esteban
 * DTO para restablecer la contrasena de la administradora a partir de
 * un codigo de recuperacion previamente enviado por correo.
 * El backend valida que el codigo este vigente, no haya sido usado
 * y que la nueva contrasena cumpla las politicas de seguridad.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {

    /**
     * @author esteban
     * Correo electronico de la administradora que solicita el cambio.
     */
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Formato de correo inválido")
    private String correo;

    /**
     * @author esteban
     * Codigo de 6 digitos recibido por correo electronico.
     * Debe contener unicamente numeros para reducir la superficie
     * de ataque antes de tocar la logica de negocio.
     */
    @NotBlank(message = "El código es obligatorio")
    @Size(min = 6, max = 6, message = "El código debe tener 6 dígitos")
    @Pattern(regexp = "[0-9]{6}", message = "El código debe contener solo números")
    private String codigo;

    /**
     * @author esteban
     * Nueva contrasena para la cuenta de la administradora.
     * Politica aplicada: minimo 8 caracteres combinando letras y numeros.
     * El backend la hashea con BCrypt antes de persistirla.
     */
    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 8, max = 100, message = "La contraseña debe tener al menos 8 caracteres")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$", message = "La contraseña debe mezclar letras y números")
    private String nuevaContrasena;
}
