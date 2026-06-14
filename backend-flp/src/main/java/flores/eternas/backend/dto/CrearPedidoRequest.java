package flores.eternas.backend.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearPedidoRequest {

    private List<ItemFlorRequest> flores;
    private List<AdicionRequest> adiciones;
    private String direccionEntrega;
    private String cedula;
    private String nombreCliente;
    private String telefono;
    private String fechaEntrega;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemFlorRequest {
        private Long tipoFlorId;
        private Long colorFlorId;
        private Integer cantidad;
    }
}
