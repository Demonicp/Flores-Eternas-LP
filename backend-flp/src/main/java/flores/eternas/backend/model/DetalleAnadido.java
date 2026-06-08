package flores.eternas.backend.model;

import flores.eternas.backend.model.id.DetalleAnadidoId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "detalle_anadido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(DetalleAnadidoId.class)
public class DetalleAnadido {

    @Id
    @ManyToOne
    @JoinColumn(name = "id_ramo")
    private Ramo ramo;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_inventario")
    private Inventario inventario;

    private Integer cantidad;
}
