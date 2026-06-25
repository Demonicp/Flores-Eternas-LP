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
    private String icono;

    public TipoFlorDTO(Long id, String descripcionFlor, BigDecimal precioUnidad, BigDecimal porcentajePorMayor, String icono) {
        this.id = id;
        this.descripcionFlor = descripcionFlor;
        this.precioUnidad = precioUnidad;
        this.porcentajePorMayor = porcentajePorMayor;
        this.icono = icono;
    }
}
