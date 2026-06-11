package flores.eternas.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class TipoFlorDTO {

    private Long id;
    private String descripcionFlor;
    private BigDecimal precioUnidad;
    private BigDecimal porcentajePorMayor;

    public TipoFlorDTO(Long id, String descripcionFlor, BigDecimal precioUnidad, BigDecimal porcentajePorMayor) {
        this.id = id;
        this.descripcionFlor = descripcionFlor;
        this.precioUnidad = precioUnidad;
        this.porcentajePorMayor = porcentajePorMayor;
    }
}
