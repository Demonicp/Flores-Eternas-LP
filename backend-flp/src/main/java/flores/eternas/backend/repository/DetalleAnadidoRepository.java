package flores.eternas.backend.repository;

import flores.eternas.backend.model.DetalleAnadido;
import flores.eternas.backend.model.id.DetalleAnadidoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleAnadidoRepository extends JpaRepository<DetalleAnadido, DetalleAnadidoId> {
}
