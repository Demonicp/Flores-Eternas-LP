package flores.eternas.backend.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import flores.eternas.backend.model.Local;

/**
 * Tests de integracion JPA para {@link LocalRepository}.
 * Verifica el filtro de locales activos usado en el desplegable de retiro.
 * Usa H2 en memoria via el perfil "test".
 *
 * @author santiago (sesion 05/08/2026 - modulo de retiro en local)
 */
@DataJpaTest
@ActiveProfiles("test")
class LocalRepositoryTest {

    @Autowired
    private LocalRepository repository;

    @BeforeEach
    void limpiar() {
        repository.deleteAll();
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
    void testGuardarYBuscarPorId() {
        Local guardado = repository.save(nuevoLocal("Local Principal", true));

        Optional<Local> encontrado = repository.findById(guardado.getId());
        assertTrue(encontrado.isPresent());
        assertEquals("Local Principal", encontrado.get().getNombreLocal());
        assertEquals("Palmira", encontrado.get().getCiudad());
    }

    @Test
    void testFindByActivoTrue_DevuelveSoloActivos() {
        repository.save(nuevoLocal("Local Central", true));
        repository.save(nuevoLocal("Local Cerrado", false));

        List<Local> activos = repository.findByActivoTrue();

        assertEquals(1, activos.size());
        assertEquals("Local Central", activos.get(0).getNombreLocal());
    }

    @Test
    void testFindByActivoTrue_DevuelveVariosActivos() {
        repository.save(nuevoLocal("Local Uno", true));
        repository.save(nuevoLocal("Local Dos", true));
        repository.save(nuevoLocal("Local Inactivo", false));

        List<Local> activos = repository.findByActivoTrue();

        assertEquals(2, activos.size());
    }

    @Test
    void testFindByActivoTrue_SinActivos_DevuelveListaVacia() {
        repository.save(nuevoLocal("Solo Inactivo", false));

        List<Local> activos = repository.findByActivoTrue();

        assertNotNull(activos);
        assertTrue(activos.isEmpty());
    }

    @Test
    void testFindByActivoTrue_SinRegistros_DevuelveListaVacia() {
        List<Local> activos = repository.findByActivoTrue();

        assertNotNull(activos);
        assertTrue(activos.isEmpty());
    }
}