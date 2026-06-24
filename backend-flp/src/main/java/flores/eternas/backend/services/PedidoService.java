package flores.eternas.backend.services;

import flores.eternas.backend.dto.CrearPedidoRequest;
import flores.eternas.backend.dto.PedidoRequestDTO;
import flores.eternas.backend.dto.PedidoResponseDTO;
import flores.eternas.backend.model.*;
import flores.eternas.backend.model.enums.Estado;
import flores.eternas.backend.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
        if (request.getFlores() == null || request.getFlores().isEmpty()) {
            throw new IllegalArgumentException("Debe incluir al menos una flor en el pedido.");
        }

        BigDecimal precioTotalFlores = BigDecimal.ZERO;
        BigDecimal precioAdiciones = BigDecimal.ZERO;

        StringBuilder adicionesJson = new StringBuilder();
        if (request.getAdiciones() != null && !request.getAdiciones().isEmpty()) {
            adicionesJson.append("[");
            for (int i = 0; i < request.getAdiciones().size(); i++) {
                var adicionReq = request.getAdiciones().get(i);
                Inventario inventario = inventarioRepository.findById(adicionReq.getInventarioId())
                        .orElseThrow(() -> new RuntimeException("Adicion no encontrada"));

                BigDecimal subtotal = inventario.getPrecioCosto()
                        .multiply(BigDecimal.valueOf(adicionReq.getCantidad()));
                precioAdiciones = precioAdiciones.add(subtotal);

                if (i > 0) adicionesJson.append(",");
                adicionesJson.append("{")
                        .append("\"nombre\":\"").append(escapeJson(inventario.getNombreInventario())).append("\",")
                        .append("\"cantidad\":").append(adicionReq.getCantidad()).append(",")
                        .append("\"precio\":").append(inventario.getPrecioCosto())
                        .append("}");
            }
            adicionesJson.append("]");
        }

        for (CrearPedidoRequest.ItemFlorRequest florReq : request.getFlores()) {
            TipoFlor tipoFlor = tipoFlorRepository.findById(florReq.getTipoFlorId())
                    .orElseThrow(() -> new RuntimeException("Tipo de flor no encontrado: " + florReq.getTipoFlorId()));

            BigDecimal subtotal = tipoFlor.getPrecioUnidad()
                    .multiply(BigDecimal.valueOf(florReq.getCantidad()));
            precioTotalFlores = precioTotalFlores.add(subtotal);
        }

        BigDecimal total = precioTotalFlores.add(precioAdiciones);

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
        LocalDate fechaEntrega = request.getFechaEntrega() != null
            ? LocalDate.parse(request.getFechaEntrega())
            : LocalDate.now().plusDays(5);
        pedido.setFechaEntrega(fechaEntrega);
        pedido.setEstado(Estado.EN_PREPARACION);
        if (persona != null) {
            pedido.setCliente(persona);
        }
        pedido = pedidoRepository.save(pedido);

        for (CrearPedidoRequest.ItemFlorRequest florReq : request.getFlores()) {
            TipoFlor tipoFlor = tipoFlorRepository.findById(florReq.getTipoFlorId())
                    .orElseThrow(() -> new RuntimeException("Tipo de flor no encontrado: " + florReq.getTipoFlorId()));

            ColorFlor colorFlor = null;
            if (florReq.getColorFlorId() != null) {
                colorFlor = colorFlorRepository.findById(florReq.getColorFlorId())
                        .orElseThrow(() -> new RuntimeException("Color de flor no encontrado: " + florReq.getColorFlorId()));
            }

            DetallePedido detallePedido = new DetallePedido();
            detallePedido.setPedido(pedido);
            detallePedido.setTipoFlor(tipoFlor.getDescripcionFlor());
            detallePedido.setColorFlor(colorFlor != null ? colorFlor.getDescripcionColor() : null);
            detallePedido.setCantidadFlores(florReq.getCantidad());
            detallePedido.setAdicionesJson(adicionesJson.length() > 0 ? adicionesJson.toString() : null);
            detallePedidoRepository.save(detallePedido);
        }

        return pedido;
    }

    @Transactional
    public PedidoResponseDTO crearPedido(PedidoRequestDTO request) {
        LocalDate hoy = LocalDate.now();
        if (request.getFechaEntrega() == null || request.getFechaEntrega().isBefore(hoy)) {
            throw new IllegalArgumentException("La fecha de entrega no puede ser anterior a hoy.");
        }

        Persona persona;
        boolean tienePersonalizados = request.getFloresPersonalizadas() != null && !request.getFloresPersonalizadas().isEmpty();

        if (tienePersonalizados && request.getCedulaCliente() != null && !request.getCedulaCliente().isBlank()) {
            persona = personaRepository.findByCedula(request.getCedulaCliente()).orElse(null);
            if (persona == null) {
                persona = new Persona();
                persona.setCedula(request.getCedulaCliente());
                persona.setNombreCliente(request.getNombreCliente());
                persona.setTelefono(request.getTelefonoCliente());
                persona = personaRepository.save(persona);
            } else {
                persona.setNombreCliente(request.getNombreCliente());
                if (request.getTelefonoCliente() != null) {
                    persona.setTelefono(request.getTelefonoCliente());
                }
                persona = personaRepository.save(persona);
            }
        } else {
            persona = new Persona();
            persona.setNombreCliente(request.getNombreCliente());
            persona.setTelefono(null);
            persona.setCedula(null);
            persona.setFechaNacimiento(null);
            persona = personaRepository.save(persona);
        }

        List<PedidoResponseDTO.ItemPedidoDTO> itemsResponse = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        List<DetallePedido> detalles = new ArrayList<>();

        // Procesar items predefinidos (ramos)
        if (request.getItems() != null) {
            for (PedidoRequestDTO.ItemPedidoDTO itemReq : request.getItems()) {
                Ramo ramo = ramoRepository.findById(itemReq.getIdRamo())
                        .orElseThrow(() -> new EntityNotFoundException("Ramo no encontrado: " + itemReq.getIdRamo()));

                BigDecimal subtotal = ramo.getPrecioRamo().multiply(BigDecimal.valueOf(itemReq.getCantidad()));
                total = total.add(subtotal);

                DetallePedido detalle = new DetallePedido();
                detalle.setRamo(ramo);
                detalle.setCantidad(itemReq.getCantidad());
                detalle.setPedido(null);
                detalles.add(detalle);

                PedidoResponseDTO.ItemPedidoDTO itemRes = new PedidoResponseDTO.ItemPedidoDTO();
                itemRes.setNombreRamo(ramo.getNombreRamo());
                itemRes.setCantidad(itemReq.getCantidad());
                itemRes.setPrecioUnitario(ramo.getPrecioRamo());
                itemsResponse.add(itemRes);
            }
        }

        // Procesar flores personalizadas
        StringBuilder adicionesJson = new StringBuilder();
        if (tienePersonalizados) {
            if (request.getAdicionesPersonalizadas() != null && !request.getAdicionesPersonalizadas().isEmpty()) {
                adicionesJson.append("[");
                for (int i = 0; i < request.getAdicionesPersonalizadas().size(); i++) {
                    var adicionReq = request.getAdicionesPersonalizadas().get(i);
                    Inventario inventario = inventarioRepository.findById(adicionReq.getInventarioId())
                            .orElseThrow(() -> new RuntimeException("Adicion no encontrada"));

                    BigDecimal subtotal = inventario.getPrecioCosto()
                            .multiply(BigDecimal.valueOf(adicionReq.getCantidad()));
                    total = total.add(subtotal);

                    if (i > 0) adicionesJson.append(",");
                    adicionesJson.append("{")
                            .append("\"nombre\":\"").append(escapeJson(inventario.getNombreInventario())).append("\",")
                            .append("\"cantidad\":").append(adicionReq.getCantidad()).append(",")
                            .append("\"precio\":").append(inventario.getPrecioCosto())
                            .append("}");
                }
                adicionesJson.append("]");
            }

            String adicionesStr = adicionesJson.length() > 0 ? adicionesJson.toString() : null;
            for (CrearPedidoRequest.ItemFlorRequest florReq : request.getFloresPersonalizadas()) {
                TipoFlor tipoFlor = tipoFlorRepository.findById(florReq.getTipoFlorId())
                        .orElseThrow(() -> new RuntimeException("Tipo de flor no encontrado: " + florReq.getTipoFlorId()));

                BigDecimal subtotal = tipoFlor.getPrecioUnidad()
                        .multiply(BigDecimal.valueOf(florReq.getCantidad()));
                total = total.add(subtotal);

                ColorFlor colorFlor = null;
                if (florReq.getColorFlorId() != null) {
                    colorFlor = colorFlorRepository.findById(florReq.getColorFlorId())
                            .orElseThrow(() -> new RuntimeException("Color de flor no encontrado: " + florReq.getColorFlorId()));
                }

                DetallePedido detalle = new DetallePedido();
                detalle.setPedido(null);
                detalle.setTipoFlor(tipoFlor.getDescripcionFlor());
                detalle.setColorFlor(colorFlor != null ? colorFlor.getDescripcionColor() : null);
                detalle.setCantidadFlores(florReq.getCantidad());
                detalle.setAdicionesJson(adicionesStr);
                detalles.add(detalle);
            }
        }

        BigDecimal montoPagado;
        String tipoPago = request.getTipoPago() != null ? request.getTipoPago() : "COMPLETO";
        if ("PARCIAL".equalsIgnoreCase(tipoPago) && "PERSONALIZADO".equalsIgnoreCase(request.getTipoPedido())) {
            montoPagado = total.multiply(BigDecimal.valueOf(0.5)).setScale(2, RoundingMode.HALF_UP);
        } else {
            montoPagado = total;
        }

        Pedido pedido = new Pedido();
        pedido.setTotalPedido(total);
        pedido.setDireccionEntrega(request.getDireccionEntrega());
        pedido.setFechaEntrega(request.getFechaEntrega());
        pedido.setCliente(persona);
        pedido.setMetodoPago(null);
        pedido.setMontoPagado(montoPagado);
        pedido.setTipoPedido(request.getTipoPedido());
        pedido.setEmailCliente(request.getEmailCliente());
        pedido.setEstado(Estado.EN_PREPARACION);
        pedido = pedidoRepository.save(pedido);

        for (DetallePedido detalle : detalles) {
            detalle.setPedido(pedido);
            detallePedidoRepository.save(detalle);

            if (detalle.getRamo() != null && detalle.getCantidad() != null) {
                Ramo ramo = detalle.getRamo();
                if (ramo.getStock() != null) {
                    int nuevoStock = Math.max(0, ramo.getStock() - detalle.getCantidad());
                    ramo.setStock(nuevoStock);
                    if (nuevoStock <= 0) {
                        ramo.setDisponible(false);
                    }
                    ramoRepository.save(ramo);
                }
            }
        }

        BigDecimal montoPendiente = total.subtract(montoPagado);

        PedidoResponseDTO response = new PedidoResponseDTO();
        response.setId(pedido.getId());
        response.setTotal(total);
        response.setMontoPagado(montoPagado);
        response.setMontoPendiente(montoPendiente);
        response.setEstado(pedido.getEstado().name());
        response.setTipoPedido(request.getTipoPedido());
        response.setFechaEntrega(request.getFechaEntrega());
        response.setItems(itemsResponse);

        if (montoPendiente.compareTo(BigDecimal.ZERO) > 0) {
            response.setMensaje("Pedido registrado. El 50% restante deberá pagarse antes de la entrega.");
        } else {
            response.setMensaje("Pedido confirmado. Pago completo recibido.");
        }

        return response;
    }

    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> listarPedidos() {
        List<Pedido> pedidos = pedidoRepository.findAll();
        return pedidos.stream().map(this::toResponseDTO).collect(java.util.stream.Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PedidoResponseDTO obtenerPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado: " + id));
        return toResponseDTO(pedido);
    }

    @Transactional
    public PedidoResponseDTO actualizarEstadoPedido(Long id, String nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado: " + id));

        Estado estado;
        try {
            estado = Estado.valueOf(nuevoEstado.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado invalido: " + nuevoEstado);
        }

        pedido.setEstado(estado);
        pedido = pedidoRepository.save(pedido);
        return toResponseDTO(pedido);
    }

    private PedidoResponseDTO toResponseDTO(Pedido pedido) {
        PedidoResponseDTO dto = new PedidoResponseDTO();
        dto.setId(pedido.getId());
        dto.setTotal(pedido.getTotalPedido());
        dto.setMontoPagado(pedido.getMontoPagado());
        dto.setMontoPendiente(pedido.getTotalPedido() != null && pedido.getMontoPagado() != null
                ? pedido.getTotalPedido().subtract(pedido.getMontoPagado())
                : BigDecimal.ZERO);
        dto.setEstado(pedido.getEstado() != null ? pedido.getEstado().name() : null);
        dto.setTipoPedido(pedido.getTipoPedido());
        dto.setFechaEntrega(pedido.getFechaEntrega());
        dto.setNombreCliente(pedido.getCliente() != null ? pedido.getCliente().getNombreCliente() : null);
        dto.setEmailCliente(pedido.getEmailCliente());
        dto.setDireccionEntrega(pedido.getDireccionEntrega());
        dto.setMensaje(null);
        dto.setItems(new ArrayList<>());
        return dto;
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
