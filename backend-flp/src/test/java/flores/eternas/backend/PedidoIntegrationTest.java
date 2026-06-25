package flores.eternas.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import flores.eternas.backend.dto.CrearPedidoRequest;
import flores.eternas.backend.dto.PedidoRequestDTO;
import flores.eternas.backend.dto.PedidoResponseDTO;
import flores.eternas.backend.exception.ValidacionException;
import flores.eternas.backend.model.CategoriaRamo;
import flores.eternas.backend.model.ColorFlor;
import flores.eternas.backend.model.Inventario;
import flores.eternas.backend.model.Pedido;
import flores.eternas.backend.model.Ramo;
import flores.eternas.backend.model.TipoFlor;
import flores.eternas.backend.model.enums.Estado;
import flores.eternas.backend.repository.CategoriaRamoRepository;
import flores.eternas.backend.repository.ColorFlorRepository;
import flores.eternas.backend.repository.InventarioRepository;
import flores.eternas.backend.repository.RamoRepository;
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

    @Autowired
    private CategoriaRamoRepository categoriaRamoRepository;

    @Autowired
    private RamoRepository ramoRepository;

    private TipoFlor rosa;
    private ColorFlor rojo;
    private Inventario corona;
    private CategoriaRamo categoria;
    private Ramo ramo;

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

        categoria = new CategoriaRamo();
        categoria.setDescripcionCategoriaRamo("Tradicional");
        categoria = categoriaRamoRepository.save(categoria);

        ramo = new Ramo();
        ramo.setNombreRamo("Ramo Tradicional");
        ramo.setPrecioRamo(new BigDecimal("15.00"));
        ramo.setStock(10);
        ramo.setDisponible(true);
        ramo.setCategoriaRamo(categoria);
        ramo = ramoRepository.save(ramo);
    }

    @Test
    void testCrearPedidoCompleto() {
        CrearPedidoRequest request = new CrearPedidoRequest();
        request.setFlores(List.of(
            new CrearPedidoRequest.ItemFlorRequest(rosa.getId(), rojo.getId(), 5)
        ));
        request.setDireccionEntrega("Calle 123, Ciudad");
        request.setAdiciones(List.of(new CrearPedidoRequest.AdicionRequest(corona.getId(), 2)));

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

    @Test
    void testCrearPedidoSinFlores_LanzaExcepcion() {
        CrearPedidoRequest request = new CrearPedidoRequest();
        request.setFlores(List.of());
        request.setDireccionEntrega("Calle sin flores");

        ValidacionException ex = assertThrows(ValidacionException.class,
                () -> pedidoService.crearPedido(request));
        assertEquals("Debe incluir al menos una flor en el pedido.", ex.getMessage());
    }

    @Test
    void testCrearPedidoFlorInexistente_LanzaExcepcion() {
        CrearPedidoRequest request = new CrearPedidoRequest();
        request.setFlores(List.of(
            new CrearPedidoRequest.ItemFlorRequest(99999L, rojo.getId(), 2)
        ));
        request.setDireccionEntrega("Calle 123");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> pedidoService.crearPedido(request));
        assertTrue(ex.getMessage().contains("Tipo de flor no encontrado"));
    }

    @Test
    void testCrearPedidoPersonalizadoPendiente() {
        CrearPedidoRequest request = new CrearPedidoRequest();
        request.setFlores(List.of(
            new CrearPedidoRequest.ItemFlorRequest(rosa.getId(), rojo.getId(), 5)
        ));
        request.setNombreCliente("Juan Pérez");
        request.setEmailCliente("juan@test.com");
        request.setDireccionEntrega("calle 12 #34-56");
        request.setFechaEntrega(LocalDate.now().plusDays(3).toString());
        request.setAdiciones(List.of(new CrearPedidoRequest.AdicionRequest(corona.getId(), 2)));

        Pedido pedido = pedidoService.crearPedidoPersonalizadoPendiente(request);

        assertNotNull(pedido);
        assertNotNull(pedido.getId());
        assertNotNull(pedido.getPagoToken());
        assertEquals(Estado.EN_PROCESO, pedido.getEstado());
        assertEquals("PERSONALIZADO", pedido.getTipoPedido());
        assertEquals(BigDecimal.ZERO, pedido.getMontoPagado());
        assertNotNull(pedido.getFechaCreacion());

        BigDecimal precioEsperado = new BigDecimal("2.50")
                .multiply(BigDecimal.valueOf(5))
                .add(new BigDecimal("5.00").multiply(BigDecimal.valueOf(2)));
        assertEquals(0, precioEsperado.compareTo(pedido.getTotalPedido()));
    }

    @Test
    void testCrearPedidoCatalogo() {
        PedidoRequestDTO request = new PedidoRequestDTO();
        request.setNombreCliente("María López");
        request.setEmailCliente("maria@test.com");
        request.setDireccionEntrega("carrera 5 #20-30");
        request.setFechaEntrega(LocalDate.now().plusDays(5));
        request.setTipoPedido("RAPIDO");
        request.setTipoPago("COMPLETO");

        PedidoRequestDTO.ItemPedidoDTO item = new PedidoRequestDTO.ItemPedidoDTO();
        item.setIdRamo(ramo.getId());
        item.setCantidad(3);
        request.setItems(List.of(item));

        PedidoResponseDTO response = pedidoService.crearPedido(request);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals(Estado.EN_PREPARACION.name(), response.getEstado());
        assertEquals("RAPIDO", response.getTipoPedido());

        BigDecimal precioEsperado = ramo.getPrecioRamo().multiply(BigDecimal.valueOf(3));
        assertEquals(0, precioEsperado.compareTo(response.getTotal()));
    }

    @Test
    void testCrearPedidoCatalogoConItemsPopulados() {
        PedidoRequestDTO request = new PedidoRequestDTO();
        request.setNombreCliente("Carlos Gómez");
        request.setEmailCliente("carlos@test.com");
        request.setDireccionEntrega("av 10 #15-25");
        request.setFechaEntrega(LocalDate.now().plusDays(2));
        request.setTipoPedido("RAPIDO");
        request.setTipoPago("COMPLETO");

        PedidoRequestDTO.ItemPedidoDTO item = new PedidoRequestDTO.ItemPedidoDTO();
        item.setIdRamo(ramo.getId());
        item.setCantidad(2);
        request.setItems(List.of(item));

        PedidoResponseDTO response = pedidoService.crearPedido(request);
        PedidoResponseDTO detalle = pedidoService.obtenerPedido(response.getId());

        assertNotNull(detalle);
        assertNotNull(detalle.getItems());
        assertFalse(detalle.getItems().isEmpty());
        assertEquals("Ramo Tradicional", detalle.getItems().get(0).getNombreRamo());
        assertEquals(Integer.valueOf(2), detalle.getItems().get(0).getCantidad());
        assertEquals(0, ramo.getPrecioRamo().compareTo(detalle.getItems().get(0).getPrecioUnitario()));
    }

    @Test
    void testCrearPedidoPersonalizadoConItemsPopulados() {
        CrearPedidoRequest request = new CrearPedidoRequest();
        request.setFlores(List.of(
            new CrearPedidoRequest.ItemFlorRequest(rosa.getId(), rojo.getId(), 5)
        ));
        request.setNombreCliente("Ana Martínez");
        request.setEmailCliente("ana@test.com");
        request.setDireccionEntrega("calle 8 #12-34");
        request.setFechaEntrega(LocalDate.now().plusDays(4).toString());

        Pedido pedido = pedidoService.crearPedidoPersonalizadoPendiente(request);
        PedidoResponseDTO detalle = pedidoService.obtenerPedido(pedido.getId());

        assertNotNull(detalle);
        assertNotNull(detalle.getItems());
        assertFalse(detalle.getItems().isEmpty());
        assertEquals("Ramo Personalizado", detalle.getItems().get(0).getNombreRamo());
        assertEquals("Rosa", detalle.getItems().get(0).getTipoFlor());
        assertEquals("Rojo", detalle.getItems().get(0).getColorFlor());
        assertEquals(Integer.valueOf(5), detalle.getItems().get(0).getCantidad());
    }

    @Test
    void testCrearPedidoPersonalizadoPendiente_FechaLanzaExcepcion() {
        CrearPedidoRequest request = new CrearPedidoRequest();
        request.setFlores(List.of(
            new CrearPedidoRequest.ItemFlorRequest(rosa.getId(), rojo.getId(), 3)
        ));
        request.setNombreCliente("Test");
        request.setEmailCliente("test@test.com");
        request.setDireccionEntrega("calle 1 #2-3");
        request.setFechaEntrega(LocalDate.now().minusDays(1).toString());

        ValidacionException ex = assertThrows(ValidacionException.class,
                () -> pedidoService.crearPedidoPersonalizadoPendiente(request));
        assertEquals("La fecha de entrega no puede ser anterior a hoy.", ex.getMessage());
    }
}
