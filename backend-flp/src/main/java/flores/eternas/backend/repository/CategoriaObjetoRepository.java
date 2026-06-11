package flores.eternas.backend.repository;

import flores.eternas.backend.model.CategoriaObjeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaObjetoRepository extends JpaRepository<CategoriaObjeto, Long> {
}
