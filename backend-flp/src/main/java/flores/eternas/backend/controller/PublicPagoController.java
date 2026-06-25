package flores.eternas.backend.controller;

import flores.eternas.backend.dto.PayUIniciarResponse;
import flores.eternas.backend.dto.PedidoResponseDTO;
import flores.eternas.backend.services.PayUService;
import flores.eternas.backend.services.PedidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/pagos/personalizado")
public class PublicPagoController {

    private final PedidoService pedidoService;
    private final PayUService payUService;

    public PublicPagoController(PedidoService pedidoService, PayUService payUService) {
        this.pedidoService = pedidoService;
        this.payUService = payUService;
    }

    @GetMapping("/{token}")
    public ResponseEntity<?> obtenerPedidoPorToken(@PathVariable String token) {
        try {
            PedidoResponseDTO pedido = pedidoService.obtenerPedidoPorToken(token);
            return ResponseEntity.ok(pedido);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{token}/pagar")
    public ResponseEntity<?> pagarSaldo(@PathVariable String token,
                                        @RequestBody(required = false) Map<String, String> body) {
        try {
            PedidoResponseDTO pedidoDTO = pedidoService.obtenerPedidoPorToken(token);
            String responseUrl = body != null ? body.get("responseUrl") : null;
            PayUIniciarResponse response = payUService.iniciarPago(
                    pedidoDTO.getId(), "SEGUNDO_PAGO", responseUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
