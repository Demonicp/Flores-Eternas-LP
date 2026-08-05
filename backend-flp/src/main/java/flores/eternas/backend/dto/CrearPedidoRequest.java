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
    @Size(max = 120, message = "Dirección demasiado larga")
    private String direccionEntrega;

    @Pattern(regexp = "^\\d{6,10}$", message = "Cédula: solo números (6-10 dígitos)")
    private String cedula;

    @NotBlank(message = "Nombre obligatorio")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "Nombre solo admite letras y espacios")
    @Size(max = 100, message = "Nombre demasiado largo")
    private String nombreCliente;

    @Pattern(regexp = "^(\\+57\\s?)?[0-9\\s\\-()]{7,15}$", message = "Teléfono no válido")
    private String telefono;

    private String fechaEntrega;

    @NotBlank(message = "Email obligatorio")
    @Email(message = "Email inválido")
    private String emailCliente;

    private String ciudad;

    private String region;

    private String responseUrl;

    /** Si es true, el cliente paga el 100% del pedido en lugar del 50% inicial. */
    private boolean pagoCompleto;

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
