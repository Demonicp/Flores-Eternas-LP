package flores.eternas.backend.controller;

import flores.eternas.backend.model.Local;
import flores.eternas.backend.services.LocalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST de locales (puntos de retiro).
 * - GET /api/locales: publico, lista de locales activos para el cliente.
 * - /api/admin/locales: CRUD protegido para la administradora.
 *
 * @author esteban
 * @author santiago (sesion 05/08/2026 - modulo de retiro en local)
 */
@RestController
@RequestMapping("/api")
public class LocalController {

    private final LocalService localService;

    public LocalController(LocalService localService) {
        this.localService = localService;
    }

    /**
     * Endpoint publico con los locales activos para el desplegable de retiro.
     *
     * @author esteban
     * @return lista de locales activos.
     */
    @GetMapping("/locales")
    public ResponseEntity<List<Local>> listarActivos() {
        return ResponseEntity.ok(localService.listarActivos());
    }

    /**
     * Endpoint admin que lista todos los locales (activos e inactivos).
     *
     * @author esteban
     * @return lista completa de locales.
     */
    @GetMapping("/admin/locales")
    public ResponseEntity<List<Local>> listarTodos() {
        return ResponseEntity.ok(localService.listarTodos());
    }

    /**
     * Endpoint admin que crea un nuevo local.
     *
     * @author esteban
     * @param local datos del local.
     * @return local creado.
     */
    @PostMapping("/admin/locales")
    public ResponseEntity<Local> crear(@RequestBody Local local) {
        return ResponseEntity.ok(localService.crear(local));
    }

    /**
     * Endpoint admin que actualiza un local existente.
     *
     * @author esteban
     * @param id identificador del local.
     * @param local datos nuevos.
     * @return local actualizado.
     */
    @PutMapping("/admin/locales/{id}")
    public ResponseEntity<Local> actualizar(@PathVariable Long id, @RequestBody Local local) {
        return ResponseEntity.ok(localService.actualizar(id, local));
    }

    /**
     * Endpoint admin que elimina un local.
     *
     * @author esteban
     * @param id identificador del local.
     * @return respuesta vacia.
     */
    @DeleteMapping("/admin/locales/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        localService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
