package flores.eternas.backend.controller;

import flores.eternas.backend.dto.PedidoResponseDTO;
import flores.eternas.backend.dto.WompiIniciarResponse;
import flores.eternas.backend.services.PedidoService;
import flores.eternas.backend.services.WompiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador público para pagos de saldo pendiente mediante token.
 * @author demonicp
 */
@RestController
@RequestMapping("/api/pagos/personalizado")
public class PublicPagoController {

    private final PedidoService pedidoService;
    private final WompiService wompiService;

    public PublicPagoController(PedidoService pedidoService, WompiService wompiService) {
        this.pedidoService = pedidoService;
        this.wompiService = wompiService;
    }

    /**
     * Obtiene los datos de un pedido por su token de pago.
     * @param token token único del pedido
     * @return datos del pedido (total, pagado, pendiente)
     * @author demonicp
     */
    @GetMapping("/{token}")
    public ResponseEntity<?> obtenerPedidoPorToken(@PathVariable String token) {
        try {
            PedidoResponseDTO pedido = pedidoService.obtenerPedidoPorToken(token);
            return ResponseEntity.ok(pedido);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Inicia el pago del saldo pendiente usando Wompi.
     * @param token token único del pedido
     * @param body  mapa opcional con responseUrl
     * @return datos para construir el formulario de Wompi
     * @author demonicp
     */
    @PostMapping("/{token}/pagar")
    public ResponseEntity<?> pagarSaldo(@PathVariable String token,
                                        @RequestBody(required = false) Map<String, String> body) {
        try {
            PedidoResponseDTO pedidoDTO = pedidoService.obtenerPedidoPorToken(token);
            String responseUrl = body != null ? body.get("responseUrl") : null;
            WompiIniciarResponse response = wompiService.iniciarPago(
                    pedidoDTO.getId(), "SEGUNDO_PAGO", responseUrl, false);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
