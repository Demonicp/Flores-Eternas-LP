package flores.eternas.backend.dto;

import java.math.BigDecimal;
import java.util.List;

import flores.eternas.backend.model.CategoriaRamo;
import flores.eternas.backend.model.DetalleRamo;
import flores.eternas.backend.model.Ramo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RamoDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private String foto;
    private BigDecimal precio;
    private CategoriaRamo categoria;
    private List<DetalleRamo> detalles;

    public RamoDTO(Ramo ramo) {
        this.id = ramo.getId();
        this.nombre = ramo.getNombreRamo();
        this.descripcion = ramo.getDescripcionRamo();
        this.foto = ramo.getFotoRamo();
        this.precio = ramo.getPrecioRamo();
        this.categoria = ramo.getCategoriaRamo();
        this.detalles = ramo.getDetallesRamo();
    }
}
