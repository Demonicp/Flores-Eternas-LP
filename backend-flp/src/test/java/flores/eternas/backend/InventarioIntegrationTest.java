package flores.eternas.backend;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import flores.eternas.backend.dto.InventarioDTO;
import flores.eternas.backend.services.InventarioService;
import jakarta.persistence.EntityNotFoundException;

@SpringBootTest
@ActiveProfiles("test")
class InventarioIntegrationTest {

    @Autowired
    private InventarioService inventarioService;

    // ===== VERDADERA 1 =====
    @Test
    void testCrearInventario() {

        InventarioDTO dto = new InventarioDTO(
                null,
                "Corona Test",
                "Producto de prueba",
                new BigDecimal("15000"),
                20
        );

        InventarioDTO creado = inventarioService.crear(dto);

        assertNotNull(creado);
        assertNotNull(creado.getId());

        assertEquals("Corona Test", creado.getNombre());
        assertEquals("Producto de prueba", creado.getDescripcion());
        assertEquals(20, creado.getStock());
    }

    // ===== VERDADERA 2 =====
    @Test
    void testActualizarInventario() {

        InventarioDTO dto = new InventarioDTO(
                null,
                "Producto Original",
                "Descripcion Original",
                new BigDecimal("10000"),
                5
        );

        InventarioDTO creado = inventarioService.crear(dto);

        InventarioDTO actualizado = new InventarioDTO(
                creado.getId(),
                "Producto Actualizado",
                "Descripcion Actualizada",
                new BigDecimal("20000"),
                10
        );

        InventarioDTO resultado =
                inventarioService.actualizar(
                        creado.getId(),
                        actualizado
                );

        assertEquals(
                "Producto Actualizado",
                resultado.getNombre()
        );

        assertEquals(
                10,
                resultado.getStock()
        );
    }

    // ===== FALSA 1 =====
    @Test
    void testObtenerInventarioInexistente() {

        assertThrows(
                EntityNotFoundException.class,
                () -> inventarioService.obtenerPorId(999999L)
        );
    }

    // ===== FALSA 2 =====
    @Test
    void testEliminarInventarioInexistente() {

        assertThrows(
                EntityNotFoundException.class,
                () -> inventarioService.eliminar(999999L)
        );
    }
}