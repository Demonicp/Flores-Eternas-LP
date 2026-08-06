package flores.eternas.backend.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import flores.eternas.backend.model.Local;
import flores.eternas.backend.repository.LocalRepository;

/**
 * Tests de integracion de {@link LocalService}.
 * Usa H2 en memoria via el perfil "test".
 *
 * @author santiago (sesion 05/08/2026 - modulo de retiro en local)
 */
@SpringBootTest
@ActiveProfiles("test")
class LocalServiceTest {

    @Autowired
    private LocalService localService;

    @Autowired
    private LocalRepository localRepository;

    @BeforeEach
    void limpiar() {
        localRepository.deleteAll();
    }

    private Local nuevoLocal(String nombre, boolean activo) {
        Local l = new Local();
        l.setNombreLocal(nombre);
        l.setDireccion("Calle 25 #28-32");
        l.setCiudad("Palmira");
        l.setRegion("Valle del Cauca");
        l.setActivo(activo);
        return l;
    }

    @Test
    void testListarActivos_FiltraInactivos() {
        localService.crear(nuevoLocal("Local Activo", true));
        localService.crear(nuevoLocal("Local Inactivo", false));

        List<Local> activos = localService.listarActivos();

        assertEquals(1, activos.size());
        assertEquals("Local Activo", activos.get(0).getNombreLocal());
    }

    @Test
    void testListarTodos_IncluyeActivosEInactivos() {
        localService.crear(nuevoLocal("Local Activo", true));
        localService.crear(nuevoLocal("Local Inactivo", false));

        List<Local> todos = localService.listarTodos();

        assertEquals(2, todos.size());
    }

    @Test
    void testCrear_PersisteLocal() {
        Local creado = localService.crear(nuevoLocal("Local Principal", true));

        assertNotNull(creado.getId());
        assertEquals("Local Principal", creado.getNombreLocal());
        assertEquals("Palmira", creado.getCiudad());
        assertTrue(creado.isActivo());
    }

    @Test
    void testActualizar_ModificaDatos() {
        Local creado = localService.crear(nuevoLocal("Local Original", true));

        Local datosNuevos = nuevoLocal("Local Renombrado", false);
        datosNuevos.setDireccion("Carrera 10 #5-15");
        datosNuevos.setCiudad("Cali");
        datosNuevos.setRegion("Valle del Cauca");

        Local actualizado = localService.actualizar(creado.getId(), datosNuevos);

        assertEquals(creado.getId(), actualizado.getId());
        assertEquals("Local Renombrado", actualizado.getNombreLocal());
        assertEquals("Carrera 10 #5-15", actualizado.getDireccion());
        assertEquals("Cali", actualizado.getCiudad());
        assertFalse(actualizado.isActivo());
    }

    @Test
    void testActualizar_IdInexistente_LanzaExcepcion() {
        Local datos = nuevoLocal("Local Fantasma", true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> localService.actualizar(9999L, datos));

        assertTrue(ex.getMessage().contains("Local no encontrado"));
    }

    @Test
    void testEliminar_EliminaLocal() {
        Local creado = localService.crear(nuevoLocal("Local a Eliminar", true));

        localService.eliminar(creado.getId());

        assertEquals(0, localRepository.count());
    }

    @Test
    void testEliminar_IdInexistente_LanzaExcepcion() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> localService.eliminar(9999L));

        assertTrue(ex.getMessage().contains("Local no encontrado"));
    }
}