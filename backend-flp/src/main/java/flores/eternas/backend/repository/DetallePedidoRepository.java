package flores.eternas.backend.repository;

import flores.eternas.backend.model.DetallePedido;
import flores.eternas.backend.model.id.DetallePedidoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, DetallePedidoId> {
}
