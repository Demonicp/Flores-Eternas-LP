package flores.eternas.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//Santiago Montenegro HU6
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetalleRamoLineaDTO {

    private Integer cantidad;
    private Long idTipoFlor;
    private Long idColorFlor;
}
