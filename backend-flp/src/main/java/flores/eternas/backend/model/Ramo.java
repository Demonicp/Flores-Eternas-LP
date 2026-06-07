package flores.eternas.backend.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "ramo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@AttributeOverride(name = "id", column = @Column(name = "id_ramo"))
public class Ramo extends AbstractEntity {

    @Column(name = "precio_ramo")
    private BigDecimal precioRamo;

    @Column(name = "descripcion_ramo")
    private String descripcionRamo;

    @Lob
    @Column(name = "foto_ramo")
    private String fotoRamo;

    @ManyToOne
    @JoinColumn(name = "id_detalle_ramo")
    private DetalleRamo detalleRamo;

    @ManyToOne
    @JoinColumn(name = "id_categoria_ramo")
    private CategoriaRamo categoriaRamo;
}
