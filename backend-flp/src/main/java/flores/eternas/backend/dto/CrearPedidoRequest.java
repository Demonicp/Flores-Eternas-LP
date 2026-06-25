package flores.eternas.backend.dto;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearPedidoRequest {

    @NotEmpty(message = "Debe incluir al menos una flor")
    private List<ItemFlorRequest> flores;

    private List<AdicionRequest> adiciones;

    @NotBlank(message = "Dirección obligatoria")
    @Pattern(regexp = "^(calle|carrera|av\\.?|avenida|transversal|diagonal|circular|cra|kr|cl|cll|tv|tr|dg|cq)\\s+\\d{1,3}[a-zA-Z]?\\s*#?\\s*\\d{1,3}[a-zA-Z]?[-–]\\d{1,3}[a-zA-Z]?$",
             flags = Pattern.Flag.CASE_INSENSITIVE,
             message = "Dirección debe ser: calle 28 #25-38")
    private String direccionEntrega;

    @Pattern(regexp = "^\\d{6,10}$", message = "Cédula: solo números (6-10 dígitos)")
    private String cedula;

    @NotBlank(message = "Nombre obligatorio")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "Nombre solo admite letras y espacios")
    @Size(max = 100, message = "Nombre demasiado largo")
    private String nombreCliente;

    @Pattern(regexp = "^(\\+57\\s?)?(3\\d{9}|60\\d{8})$", message = "Teléfono: +57 3001234567 o 6012345678")
    private String telefono;

    private String fechaEntrega;

    @NotBlank(message = "Email obligatorio")
    @Email(message = "Email inválido")
    private String emailCliente;

    private String responseUrl;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemFlorRequest {
        private Long tipoFlorId;
        private Long colorFlorId;
        private Integer cantidad;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdicionRequest {
        private Long inventarioId;
        private Integer cantidad;
    }
}
