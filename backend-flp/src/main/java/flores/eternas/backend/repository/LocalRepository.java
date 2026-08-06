package flores.eternas.backend.repository;

import flores.eternas.backend.model.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de la entidad {@link Local}.
 *
 * @author esteban
 * @author santiago (sesion 05/08/2026 - modulo de retiro en local)
 */
@Repository
public interface LocalRepository extends JpaRepository<Local, Long> {

    /**
     * Retorna los locales activos, usados en el desplegable de retiro del cliente.
     *
     * @author esteban
     * @return lista de locales con {@code activo = true}, sin orden garantizado.
     */
    List<Local> findByActivoTrue();
}