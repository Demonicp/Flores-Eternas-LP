package flores.eternas.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import flores.eternas.backend.model.Ramo;

//Santiago Montenegro HU6
@Repository
public interface RamoRepository extends JpaRepository<Ramo, Long> {
}
