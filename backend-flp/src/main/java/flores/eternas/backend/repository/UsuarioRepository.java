package flores.eternas.backend.repository;

import flores.eternas.backend.model.Usuario;
import flores.eternas.backend.model.enums.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author esteban
 * Repository para la entidad Usuario.
 * Proporciona metodos de acceso a datos para usuarios del sistema.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * @author esteban
     * Busca un usuario por su correo electronico.
     * @param correoElectronico Correo del usuario a buscar.
     * @return Optional con el usuario si existe, vacio si no.
     */
    Optional<Usuario> findByCorreoElectronico(String correoElectronico);

    /**
     * @author esteban
     * Verifica si existe un usuario con el correo especificado.
     * @param correoElectronico Correo a verificar.
     * @return true si existe, false en caso contrario.
     */
    boolean existsByCorreoElectronico(String correoElectronico);

    /**
     * @author esteban
     * Verifica si existe al menos un usuario con el rol especificado.
     * Utilizado para verificar si ya existe un administrador registrado.
     * @param rol Rol a verificar.
     * @return true si existe al menos un usuario con ese rol, false en caso contrario.
     */
    boolean existsByRol(Rol rol);
}