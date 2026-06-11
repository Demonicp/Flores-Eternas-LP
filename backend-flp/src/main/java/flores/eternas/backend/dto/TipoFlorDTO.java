package flores.eternas.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

//Santiago Montenegro HU6
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TipoFlorDTO {

    private Long id;
    private String descripcionFlor;
    private BigDecimal precioUnidad;
    private BigDecimal porcentajePorMayor;
}
