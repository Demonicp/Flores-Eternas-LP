package flores.eternas.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author esteban
 * DTO para registrar un nuevo usuario administrador en el sistema.
 * Solo funciona cuando no existe ningun otro administrador registrado.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    /**
     * @author esteban
     * Correo electronico unico del nuevo administrador. Debe ser un email valido.
     */
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Formato de correo inválido")
    private String correo;

    /**
     * @author esteban
     * Contrasena del usuario. Debe tener al menos 6 caracteres.
     */
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String contrasena;

    /**
     * @author esteban
     * Nombre completo del administrador.
     */
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
}