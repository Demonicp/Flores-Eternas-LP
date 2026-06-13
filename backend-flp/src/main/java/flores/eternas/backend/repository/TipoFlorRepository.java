package flores.eternas.backend.repository;

import flores.eternas.backend.model.TipoFlor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipoFlorRepository extends JpaRepository<TipoFlor, Long> {

    List<TipoFlor> findAllByOrderByDescripcionFlorAsc();
}
