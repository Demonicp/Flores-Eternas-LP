package flores.eternas.backend.repository;

import flores.eternas.backend.model.CategoriaRamo;
import flores.eternas.backend.model.Ramo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RamoRepository extends JpaRepository<Ramo, Long> {

    List<Ramo> findByDisponibleTrue();

    List<Ramo> findByCategoriaRamoOrderByNombreRamoAsc(CategoriaRamo categoriaRamo);

    List<Ramo> findByCategoriaRamoAndDisponibleTrue(CategoriaRamo categoriaRamo);

    boolean existsByDisponibleTrue();
}
