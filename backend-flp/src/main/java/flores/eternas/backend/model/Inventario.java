package flores.eternas.backend.model;

import flores.eternas.backend.model.enums.Estado;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "inventario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@AttributeOverride(name = "id", column = @Column(name = "id_inventario"))
public class Inventario extends AbstractEntity {

    @Column(name = "nombre_inventario")
    private String nombreInventario;

    @Column(name = "descripcion_inventario")
    private String descripcionInventario;

    private Integer stock;

    @Column(name = "precio_costo")
    private BigDecimal precioCosto;

    @Enumerated(EnumType.STRING)
    private Estado estado;

    @ManyToOne
    @JoinColumn(name = "id_categoria")
    private CategoriaObjeto categoria;
}
