package flores.eternas.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

//Santiago Montenegro HU6
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RamoRequestDTO {

    private String nombreRamo;
    private BigDecimal precioRamo;
    private String descripcionRamo;
    private String fotoRamo;
    private Long idCategoriaRamo;
    private List<DetalleRamoLineaDTO> detallesRamo;
}
