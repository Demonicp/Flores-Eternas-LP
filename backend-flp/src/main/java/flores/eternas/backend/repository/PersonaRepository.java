package flores.eternas.backend.repository;

import flores.eternas.backend.model.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author esteban
 * Repository para la entidad Persona.
 * Proporciona metodos de acceso a datos para personas del sistema.
 */
@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {

}