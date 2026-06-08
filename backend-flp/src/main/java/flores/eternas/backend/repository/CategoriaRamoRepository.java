package flores.eternas.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import flores.eternas.backend.model.CategoriaRamo;

//Santiago Montenegro HU6
@Repository
public interface CategoriaRamoRepository extends JpaRepository<CategoriaRamo, Long> {
}
