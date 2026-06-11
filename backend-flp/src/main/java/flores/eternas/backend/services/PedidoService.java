package flores.eternas.backend.services;

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
@Transactional
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PersonaRepository personaRepository;
    private final RamoRepository ramoRepository;
    private final DetallePedidoRepository detallePedidoRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         PersonaRepository personaRepository,
                         RamoRepository ramoRepository,
                         DetallePedidoRepository detallePedidoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.personaRepository = personaRepository;
        this.ramoRepository = ramoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
    }

    public PedidoResponseDTO crearPedido(PedidoRequestDTO request) {
        LocalDate hoy = LocalDate.now();
        if (request.getFechaEntrega() == null || request.getFechaEntrega().isBefore(hoy.plusDays(5))) {
            throw new IllegalArgumentException("La fecha de entrega debe ser al menos 5 días después de hoy.");
        }

        Persona persona = new Persona();
        persona.setNombreCliente(request.getNombreCliente());
        persona.setTelefono(null);
        persona.setCedula(null);
        persona.setFechaNacimiento(null);
        persona = personaRepository.save(persona);

        List<PedidoResponseDTO.ItemPedidoDTO> itemsResponse = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        List<DetallePedido> detalles = new ArrayList<>();
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
}
