package flores.eternas.backend.repository;

import flores.eternas.backend.model.CategoriaRamo;
import flores.eternas.backend.model.Ramo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RamoRepository extends JpaRepository<Ramo, Long> {

    List<Ramo> findByDisponibleTrue();

    List<Ramo> findByCategoriaRamoOrderByNombreRamoAsc(CategoriaRamo categoriaRamo);

    List<Ramo> findByCategoriaRamoAndDisponibleTrue(CategoriaRamo categoriaRamo);

    long countByCategoriaRamo(CategoriaRamo categoriaRamo);

    boolean existsByDisponibleTrue();

    @Query("SELECT DISTINCT r FROM Ramo r LEFT JOIN FETCH r.categoriaRamo WHERE r.disponible = true ORDER BY r.nombreRamo ASC")
    List<Ramo> findAllDisponiblesWithCategoria();

    @Query("SELECT DISTINCT r FROM Ramo r LEFT JOIN FETCH r.categoriaRamo WHERE r.categoriaRamo = :cat AND r.disponible = true ORDER BY r.nombreRamo ASC")
    List<Ramo> findByCategoriaWithDetails(@Param("cat") CategoriaRamo cat);
}
