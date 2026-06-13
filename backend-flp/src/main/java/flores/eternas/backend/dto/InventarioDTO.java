package flores.eternas.backend.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventarioDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precioCosto;
    private Integer stock;
}
