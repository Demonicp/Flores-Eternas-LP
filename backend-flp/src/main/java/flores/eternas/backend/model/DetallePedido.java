package flores.eternas.backend.model;

import flores.eternas.backend.model.id.DetallePedidoId;
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
@Table(name = "detalle_pedido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(DetallePedidoId.class)
public class DetallePedido {

    @Id
    @ManyToOne
    @JoinColumn(name = "id_pedido")
    private Pedido pedido;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_ramo")
    private Ramo ramo;

    private Integer cantidad;
}
