package flores.eternas.backend.repository;

import flores.eternas.backend.model.CategoriaRamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriaRamoRepository extends JpaRepository<CategoriaRamo, Long> {

    Optional<CategoriaRamo> findByDescripcionCategoriaRamoIgnoreCase(String descripcion);
}
