package flores.eternas.backend.controller;

import flores.eternas.backend.dto.CrearPedidoRequest;
import flores.eternas.backend.dto.WompiIniciarResponse;
import flores.eternas.backend.model.Pedido;
import flores.eternas.backend.services.PedidoService;
import flores.eternas.backend.services.WompiService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST para integración con Wompi Web Checkout.
 * @author demonicp
 */
@RestController
@RequestMapping("/api/pagos/wompi")
public class WompiController {

    private final WompiService wompiService;
    private final PedidoService pedidoService;

    public WompiController(WompiService wompiService, PedidoService pedidoService) {
        this.wompiService = wompiService;
        this.pedidoService = pedidoService;
    }

    /**
     * Inicia un pago Wompi para un pedido personalizado.
     * @param request datos del pedido (flores, adiciones, datos del cliente)
     * @return respuesta con datos para construir el formulario de Wompi
     * @author demonicp
     */
    @PostMapping("/iniciar")
    public ResponseEntity<?> iniciarPago(@Valid @RequestBody CrearPedidoRequest request) {
        try {
            Pedido pedido = pedidoService.crearPedidoPersonalizadoPendiente(request);
            WompiIniciarResponse response = wompiService.iniciarPago(
                    pedido.getId(), "PRIMER_PAGO", request.getResponseUrl(), request.isPagoCompleto());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Recibe el webhook de Wompi con la actualización del estado de una transacción.
     * Wompi envía un POST con el payload JSON de la transacción.
     * @param payload cuerpo del webhook
     * @return confirmación de recepción
     * @author demonicp
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(@RequestBody Map<String, Object> payload) {
        try {
            Map<String, Object> data = (Map<String, Object>) payload.get("data");
            if (data != null) {
                Map<String, Object> transaction = (Map<String, Object>) data.get("transaction");
                if (transaction != null) {
                    WompiService.WompiTransaction tx = new WompiService.WompiTransaction();
                    tx.setId((String) transaction.get("id"));
                    tx.setReference((String) transaction.get("reference"));
                    tx.setStatus((String) transaction.get("status"));
                    if (transaction.get("amount_in_cents") instanceof Number) {
                        tx.setAmountInCents(((Number) transaction.get("amount_in_cents")).longValue());
                    }
                    wompiService.procesarWebhook(tx);
                }
            }
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            log.error("Error procesando webhook Wompi", e);
            return ResponseEntity.ok("OK");
        }
    }

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(WompiController.class);
}
