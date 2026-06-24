package flores.eternas.backend;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import flores.eternas.backend.dto.AdicionRequest;
import flores.eternas.backend.dto.CrearPedidoRequest;
import flores.eternas.backend.dto.PedidoResponseDTO;
import flores.eternas.backend.model.ColorFlor;
import flores.eternas.backend.model.Inventario;
import flores.eternas.backend.model.Pedido;
import flores.eternas.backend.model.TipoFlor;
import flores.eternas.backend.model.enums.Estado;
import flores.eternas.backend.repository.ColorFlorRepository;
import flores.eternas.backend.repository.InventarioRepository;
import flores.eternas.backend.repository.TipoFlorRepository;
import flores.eternas.backend.services.PedidoService;

import jakarta.persistence.EntityNotFoundException;

@SpringBootTest
@ActiveProfiles("test")
class PedidoAdminTest {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private TipoFlorRepository tipoFlorRepository;

    @Autowired
    private ColorFlorRepository colorFlorRepository;

    @Autowired
    private InventarioRepository inventarioRepository;

    private TipoFlor rosa;
    private ColorFlor rojo;
    private Inventario corona;

    @BeforeEach
    void setUp() {
        rosa = new TipoFlor();
        rosa.setDescripcionFlor("Rosa");
        rosa.setPrecioUnidad(new BigDecimal("2.50"));
        rosa.setPorcentajePorMayor(new BigDecimal("0.10"));
        rosa = tipoFlorRepository.save(rosa);

        rojo = new ColorFlor();
        rojo.setDescripcionColor("Rojo");
        rojo = colorFlorRepository.save(rojo);

        corona = new Inventario();
        corona.setNombreInventario("Corona");
        corona.setDescripcionInventario("Corona decorativa");
        corona.setStock(20);
        corona.setPrecioCosto(new BigDecimal("5.00"));
        corona.setEstado(Estado.DISPONIBLE);
        corona = inventarioRepository.save(corona);
    }

    private Pedido crearPedidoBase() {
        CrearPedidoRequest request = new CrearPedidoRequest();
        request.setFlores(List.of(
            new CrearPedidoRequest.ItemFlorRequest(rosa.getId(), rojo.getId(), 3)
        ));
        request.setDireccionEntrega("Calle Principal 123");
        return pedidoService.crearPedido(request);
    }

    /* ─── Tests positivos ─── */

    @Test
    void testListarPedidos_DevuelveListaConPedidoCreado() {
        Pedido pedidoCreado = crearPedidoBase();

        List<PedidoResponseDTO> lista = pedidoService.listarPedidos();

        assertNotNull(lista);
        assertFalse(lista.isEmpty());
        assertTrue(lista.stream().anyMatch(p -> p.getId().equals(pedidoCreado.getId())));
    }

    @Test
    void testActualizarEstado_CambiaCorrectamente() {
        Pedido pedidoCreado = crearPedidoBase();
        assertEquals(Estado.EN_PREPARACION, pedidoCreado.getEstado());

        PedidoResponseDTO actualizado = pedidoService.actualizarEstadoPedido(pedidoCreado.getId(), "ENTREGADO");

        assertNotNull(actualizado);
        assertEquals("ENTREGADO", actualizado.getEstado());
    }

    /* ─── Tests negativos ─── */

    @Test
    void testObtenerPedidoNoExistente_LanzaExcepcion() {
        assertThrows(EntityNotFoundException.class,
            () -> pedidoService.obtenerPedido(99999L));
    }

    @Test
    void testActualizarEstadoInvalido_LanzaExcepcion() {
        Pedido pedidoCreado = crearPedidoBase();

        assertThrows(IllegalArgumentException.class,
            () -> pedidoService.actualizarEstadoPedido(pedidoCreado.getId(), "ESTADO_INEXISTENTE"));
    }
}
