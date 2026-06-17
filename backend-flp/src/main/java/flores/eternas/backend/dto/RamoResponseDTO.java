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
public class RamoResponseDTO {

    private Long id;
    private String nombreRamo;
    private BigDecimal precioRamo;
    private String descripcionRamo;
    private String fotoRamo;
    private Boolean disponible;
    private Integer stock;
    private CategoriaRamoDTO categoria;
    private List<DetalleRamoResponseDTO> detallesRamo;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetalleRamoResponseDTO {
        private Long id;
        private Integer cantidad;
        private TipoFlorDTO tipoFlor;
        private ColorFlorDTO colorFlor;
    }
}
