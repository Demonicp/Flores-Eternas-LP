package flores.eternas.backend.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "tipo_flor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@AttributeOverride(name = "id", column = @Column(name = "id_tipo_flor"))
public class TipoFlor extends AbstractEntity {

    @Column(name = "descripcion_flor")
    private String descripcionFlor;

    @Column(name = "precio_unidad")
    private BigDecimal precioUnidad;

    @Column(name = "porcentaje_por_mayor")
    private BigDecimal porcentajePorMayor;
}
