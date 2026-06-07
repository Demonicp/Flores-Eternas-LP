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
import java.time.LocalDate;

@Entity
@Table(name = "cierre_caja")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@AttributeOverride(name = "id", column = @Column(name = "id_caja"))
public class CierreCaja extends AbstractEntity {

    @Column(name = "fecha_de_cierre")
    private LocalDate fechaDeCierre;

    private BigDecimal inversion;

    @Column(name = "mano_obra")
    private BigDecimal manoObra;

    private BigDecimal ganancia;
}
