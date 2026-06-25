package flores.eternas.backend.controller;

import flores.eternas.backend.dto.CrearPedidoRequest;
import flores.eternas.backend.dto.PayUIniciarResponse;
import flores.eternas.backend.model.Pedido;
import flores.eternas.backend.services.PayUService;
import flores.eternas.backend.services.PedidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/pagos/payu")
public class PayUController {

    private final PayUService payUService;
    private final PedidoService pedidoService;

    public PayUController(PayUService payUService, PedidoService pedidoService) {
        this.payUService = payUService;
        this.pedidoService = pedidoService;
    }

    @PostMapping("/iniciar")
    public ResponseEntity<?> iniciarPago(@RequestBody CrearPedidoRequest request) {
        try {
            Pedido pedido = pedidoService.crearPedidoPersonalizadoPendiente(request);
            PayUIniciarResponse response = payUService.iniciarPago(
                    pedido.getId(), "PRIMER_PAGO", request.getResponseUrl());

            response.setPedidoId(pedido.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/confirmacion")
    public ResponseEntity<String> confirmacion(@RequestParam Map<String, String> params) {
        payUService.confirmarPago(params);
        return ResponseEntity.ok("OK");
    }
}
