package flores.eternas.backend.repository;

import flores.eternas.backend.model.Inventario;
import flores.eternas.backend.model.enums.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {

    List<Inventario> findByEstado(Estado estado);
}
