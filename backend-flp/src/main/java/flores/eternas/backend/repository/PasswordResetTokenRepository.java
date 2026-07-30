package flores.eternas.backend.repository;

import flores.eternas.backend.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author esteban
 * Repositorio JPA para la entidad PasswordResetToken.
 * Proporciona metodos de acceso a datos para los codigos de recuperacion
 * de contrasena generados para la administradora del sistema.
 */
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    /**
     * @author esteban
     * Obtiene el token activo (no usado) mas reciente asociado al correo.
     * Se utiliza para validar el codigo ingresado por el usuario y para
     * localizar el token a invalidar al solicitar un codigo nuevo.
     * @param correo Correo electronico asociado al token.
     * @return Optional con el token activo mas reciente, vacio si no hay ninguno.
     */
    Optional<PasswordResetToken> findFirstByCorreoAndUsadoFalseOrderByCreadoEnDesc(String correo);

    /**
     * @author esteban
     * Elimina todos los tokens asociados a un correo electronico.
     * Pensado para limpieza periodica o para uso en testing.
     * @param correo Correo electronico cuyos tokens se eliminaran.
     */
    void deleteByCorreo(String correo);
}
