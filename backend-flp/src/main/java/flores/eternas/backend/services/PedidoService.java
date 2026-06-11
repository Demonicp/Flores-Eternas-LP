package flores.eternas.backend.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import flores.eternas.backend.dto.CrearPedidoRequest;
import flores.eternas.backend.model.CategoriaRamo;
import flores.eternas.backend.model.ColorFlor;
import flores.eternas.backend.model.DetalleAnadido;
import flores.eternas.backend.model.DetallePedido;
import flores.eternas.backend.model.DetalleRamo;
import flores.eternas.backend.model.Inventario;
import flores.eternas.backend.model.Pedido;
import flores.eternas.backend.model.Persona;
import flores.eternas.backend.model.Ramo;
import flores.eternas.backend.model.TipoFlor;
import flores.eternas.backend.model.enums.Estado;
import flores.eternas.backend.repository.CategoriaRamoRepository;
import flores.eternas.backend.repository.ColorFlorRepository;
import flores.eternas.backend.repository.DetalleAnadidoRepository;
import flores.eternas.backend.repository.DetallePedidoRepository;
import flores.eternas.backend.repository.InventarioRepository;
import flores.eternas.backend.repository.PedidoRepository;
import flores.eternas.backend.repository.PersonaRepository;
import flores.eternas.backend.repository.RamoRepository;
import flores.eternas.backend.repository.TipoFlorRepository;

@Service
public class PedidoService {

    private final TipoFlorRepository tipoFlorRepository;
    private final ColorFlorRepository colorFlorRepository;
    private final InventarioRepository inventarioRepository;
    private final RamoRepository ramoRepository;
    private final PedidoRepository pedidoRepository;
    private final CategoriaRamoRepository categoriaRamoRepository;
    private final DetalleAnadidoRepository detalleAnadidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final PersonaRepository personaRepository;

    public PedidoService(
            TipoFlorRepository tipoFlorRepository,
            ColorFlorRepository colorFlorRepository,
            InventarioRepository inventarioRepository,
            RamoRepository ramoRepository,
            PedidoRepository pedidoRepository,
            CategoriaRamoRepository categoriaRamoRepository,
            DetalleAnadidoRepository detalleAnadidoRepository,
            DetallePedidoRepository detallePedidoRepository,
            PersonaRepository personaRepository) {
        this.tipoFlorRepository = tipoFlorRepository;
        this.colorFlorRepository = colorFlorRepository;
        this.inventarioRepository = inventarioRepository;
        this.ramoRepository = ramoRepository;
        this.pedidoRepository = pedidoRepository;
        this.categoriaRamoRepository = categoriaRamoRepository;
        this.detalleAnadidoRepository = detalleAnadidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.personaRepository = personaRepository;
    }

    @Transactional
    public Pedido crearPedido(CrearPedidoRequest request) {
        TipoFlor tipoFlor = tipoFlorRepository.findById(request.getTipoFlorId())
                .orElseThrow(() -> new RuntimeException("Tipo de flor no encontrado"));

        ColorFlor colorFlor = colorFlorRepository.findById(request.getColorFlorId())
                .orElseThrow(() -> new RuntimeException("Color de flor no encontrado"));

        CategoriaRamo categoria = categoriaRamoRepository.findAll().stream()
                .findFirst()
                .orElseGet(() -> {
                    CategoriaRamo cat = new CategoriaRamo();
                    cat.setDescripcionCategoriaRamo("Personalizado");
                    return categoriaRamoRepository.save(cat);
                });

        BigDecimal precioFlores = tipoFlor.getPrecioUnidad()
                .multiply(BigDecimal.valueOf(request.getCantidad()));
        BigDecimal precioAdiciones = BigDecimal.ZERO;

        DetalleRamo detalleRamo = new DetalleRamo();
        detalleRamo.setCantidad(request.getCantidad());
        detalleRamo.setTipoFlor(tipoFlor);
        detalleRamo.setColorFlor(colorFlor);

        List<DetalleRamo> detalles = new ArrayList<>();
        detalles.add(detalleRamo);

        Ramo ramo = new Ramo();
        ramo.setNombreRamo("Ramo Personalizado - " + tipoFlor.getDescripcionFlor());
        ramo.setDescripcionRamo(
                "Ramo personalizado con " + request.getCantidad() + " " + tipoFlor.getDescripcionFlor());
        ramo.setCategoriaRamo(categoria);
        ramo.setDetallesRamo(detalles);
        ramo = ramoRepository.save(ramo);

        detalleRamo.setRamo(ramo);
        ramoRepository.save(ramo);

        if (request.getAdiciones() != null) {
            for (var adicionReq : request.getAdiciones()) {
                Inventario inventario = inventarioRepository.findById(adicionReq.getInventarioId())
                        .orElseThrow(() -> new RuntimeException("Adicion no encontrada"));

                DetalleAnadido da = new DetalleAnadido();
                da.setRamo(ramo);
                da.setInventario(inventario);
                da.setCantidad(adicionReq.getCantidad());
                detalleAnadidoRepository.save(da);

                BigDecimal subtotal = inventario.getPrecioCosto()
                        .multiply(BigDecimal.valueOf(adicionReq.getCantidad()));
                precioAdiciones = precioAdiciones.add(subtotal);
            }
        }

        BigDecimal total = precioFlores.add(precioAdiciones);
        ramo.setPrecioRamo(total);
        ramoRepository.save(ramo);

        Persona persona = null;
        if (request.getCedula() != null && !request.getCedula().isBlank()) {
            persona = personaRepository.findByCedula(request.getCedula()).orElse(null);
            if (persona == null) {
                persona = new Persona();
                persona.setCedula(request.getCedula());
                persona.setNombreCliente(request.getNombreCliente());
                persona.setTelefono(request.getTelefono());
                persona = personaRepository.save(persona);
            } else {
                if (request.getNombreCliente() != null) {
                    persona.setNombreCliente(request.getNombreCliente());
                }
                if (request.getTelefono() != null) {
                    persona.setTelefono(request.getTelefono());
                }
                persona = personaRepository.save(persona);
            }
        }

        Pedido pedido = new Pedido();
        pedido.setTotalPedido(total);
        pedido.setDireccionEntrega(request.getDireccionEntrega());
        pedido.setFechaEntrega(LocalDate.now().plusDays(3));
        pedido.setEstado(Estado.EN_PREPARACION);
        if (persona != null) {
            pedido.setCliente(persona);
        }
        pedido = pedidoRepository.save(pedido);

        DetallePedido detallePedido = new DetallePedido();
        detallePedido.setPedido(pedido);
        detallePedido.setRamo(ramo);
        detallePedido.setCantidad(1);
        detallePedidoRepository.save(detallePedido);

        return pedido;
    }
}
