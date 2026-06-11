package flores.eternas.backend.repository;

import flores.eternas.backend.model.CategoriaRamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRamoRepository extends JpaRepository<CategoriaRamo, Long> {
}
