package flores.eternas.backend.repository;

import flores.eternas.backend.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    Optional<Pedido> findByPagoToken(String pagoToken);
    List<Pedido> findAllByOrderByFechaCreacionDesc();
}
