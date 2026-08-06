package flores.eternas.backend.services;

import flores.eternas.backend.model.Local;
import flores.eternas.backend.repository.LocalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Logica de negocio de los locales (puntos de retiro).
 * El cliente elige un local en el checkout y el pedido guarda internamente
 * su direccion, sin alterar la logica existente de direcciones de Pedido.
 *
 * @author esteban
 * @author santiago (sesion 05/08/2026 - modulo de retiro en local)
 */
@Service
public class LocalService {

    private final LocalRepository localRepository;

    public LocalService(LocalRepository localRepository) {
        this.localRepository = localRepository;
    }

    /**
     * Lista los locales activos para mostrarlos al cliente.
     *
     * @author esteban
     * @return lista de locales activos.
     */
    public List<Local> listarActivos() {
        return localRepository.findByActivoTrue();
    }

    /**
     * Lista todos los locales (incluye inactivos) para la administracion.
     *
     * @author esteban
     * @return lista completa de locales.
     */
    public List<Local> listarTodos() {
        return localRepository.findAll();
    }

    /**
     * Crea un nuevo local.
     *
     * @author esteban
     * @param local datos del local a crear.
     * @return local persistido.
     */
    public Local crear(Local local) {
        return localRepository.save(local);
    }

    /**
     * Actualiza los datos de un local existente.
     *
     * @author esteban
     * @param id identificador del local.
     * @param local datos nuevos del local.
     * @return local actualizado.
     * @throws RuntimeException si el local no existe.
     */
    public Local actualizar(Long id, Local local) {
        Local existente = localRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Local no encontrado: " + id));
        existente.setNombreLocal(local.getNombreLocal());
        existente.setDireccion(local.getDireccion());
        existente.setCiudad(local.getCiudad());
        existente.setRegion(local.getRegion());
        existente.setActivo(local.isActivo());
        return localRepository.save(existente);
    }

    /**
     * Elimina un local por su identificador.
     *
     * @author esteban
     * @param id identificador del local.
     * @throws RuntimeException si el local no existe.
     */
    public void eliminar(Long id) {
        if (!localRepository.existsById(id)) {
            throw new RuntimeException("Local no encontrado: " + id);
        }
        localRepository.deleteById(id);
    }
}
