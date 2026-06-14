package flores.eternas.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import flores.eternas.backend.dto.AdicionRequest;
import flores.eternas.backend.dto.CrearPedidoRequest;
import flores.eternas.backend.model.ColorFlor;
import flores.eternas.backend.model.Inventario;
import flores.eternas.backend.model.Pedido;
import flores.eternas.backend.model.TipoFlor;
import flores.eternas.backend.model.enums.Estado;
import flores.eternas.backend.repository.ColorFlorRepository;
import flores.eternas.backend.repository.InventarioRepository;
import flores.eternas.backend.repository.TipoFlorRepository;
import flores.eternas.backend.services.PedidoService;

@SpringBootTest
@ActiveProfiles("test")
class PedidoIntegrationTest {

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

    @Test
    void testCrearPedidoCompleto() {
        CrearPedidoRequest request = new CrearPedidoRequest();
        request.setFlores(List.of(
            new CrearPedidoRequest.ItemFlorRequest(rosa.getId(), rojo.getId(), 5)
        ));
        request.setDireccionEntrega("Calle 123, Ciudad");
        request.setAdiciones(List.of(new AdicionRequest(corona.getId(), 2)));

        Pedido pedido = pedidoService.crearPedido(request);

        assertNotNull(pedido);
        assertNotNull(pedido.getId());
        assertEquals(Estado.EN_PREPARACION, pedido.getEstado());
        assertEquals("Calle 123, Ciudad", pedido.getDireccionEntrega());

        BigDecimal precioEsperado = new BigDecimal("2.50")
                .multiply(BigDecimal.valueOf(5))
                .add(new BigDecimal("5.00").multiply(BigDecimal.valueOf(2)));
        assertEquals(0, precioEsperado.compareTo(pedido.getTotalPedido()),
                "El total debe ser (2.50*5) + (5.00*2) = 22.50");
    }

    @Test
    void testCrearPedidoSinAdiciones() {
        CrearPedidoRequest request = new CrearPedidoRequest();
        request.setFlores(List.of(
            new CrearPedidoRequest.ItemFlorRequest(rosa.getId(), rojo.getId(), 3)
        ));
        request.setDireccionEntrega("Av. Principal 456");

        Pedido pedido = pedidoService.crearPedido(request);

        assertNotNull(pedido);
        assertNotNull(pedido.getId());

        BigDecimal precioEsperado = new BigDecimal("2.50").multiply(BigDecimal.valueOf(3));
        assertEquals(0, precioEsperado.compareTo(pedido.getTotalPedido()),
                "El total debe ser 2.50*3 = 7.50");
    }
}
