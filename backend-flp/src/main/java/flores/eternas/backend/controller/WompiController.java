package flores.eternas.backend.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import flores.eternas.backend.dto.CrearPedidoRequest;
import flores.eternas.backend.dto.PedidoRequestDTO;
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
    private final ObjectMapper objectMapper = new ObjectMapper();

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
     * Inicia un pago Wompi para un pedido desde el carrito (rápido).
     * Acepta ramos predefinidos y flores personalizadas.
     * @param request datos del pedido (items, flores personalizadas, datos del cliente)
     * @return respuesta con datos para construir el formulario de Wompi
     * @author esteban
     */
    @PostMapping("/iniciar-rapido")
    public ResponseEntity<?> iniciarPagoRapido(@Valid @RequestBody PedidoRequestDTO request) {
        try {
            Pedido pedido = pedidoService.crearPedidoPendiente(request);
            WompiIniciarResponse response = wompiService.iniciarPago(
                    pedido.getId(), "PRIMER_PAGO", request.getResponseUrl(), true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Recibe el webhook de Wompi con la actualización del estado de una transacción.
     * Verifica la firma HMAC-SHA256 con WOMPI_PRIVATE_KEY antes de procesar.
     * Wompi envía un POST con el header X-Signature y el payload JSON de la transacción.
     * @param signature valor del header X-Signature
     * @param rawBody cuerpo crudo del webhook (JSON)
     * @return confirmación de recepción
     * @author demonicp
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(
            @RequestHeader("X-Signature") String signature,
            @RequestBody String rawBody) {
        try {
            if (!wompiService.validarFirmaWebhook(signature, rawBody)) {
                log.warn("Webhook rechazado: firma inválida");
                return ResponseEntity.status(401).body("Firma inválida");
            }

            Map<String, Object> payload = objectMapper.readValue(rawBody, new TypeReference<Map<String, Object>>() {});
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
