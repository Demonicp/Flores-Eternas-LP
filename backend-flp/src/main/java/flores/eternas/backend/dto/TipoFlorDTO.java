package flores.eternas.backend.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoFlorDTO {

    private Long id;
    private String descripcion;
    private BigDecimal precioUnidad;
}
