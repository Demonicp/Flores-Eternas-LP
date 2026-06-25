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
    private String iconoColor;

    public TipoFlorDTO(Long id, String descripcionFlor, BigDecimal precioUnidad, BigDecimal porcentajePorMayor, String icono, String iconoColor) {
        this.id = id;
        this.descripcionFlor = descripcionFlor;
        this.precioUnidad = precioUnidad;
        this.porcentajePorMayor = porcentajePorMayor;
        this.icono = icono;
        this.iconoColor = iconoColor;
    }
}
