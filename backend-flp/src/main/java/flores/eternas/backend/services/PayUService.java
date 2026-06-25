package flores.eternas.backend.services;

import flores.eternas.backend.dto.PayUIniciarResponse;
import flores.eternas.backend.model.Pedido;
import flores.eternas.backend.model.enums.Estado;
import flores.eternas.backend.repository.PedidoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class PayUService {

    private static final Logger log = LoggerFactory.getLogger(PayUService.class);
    private static final String CURRENCY = "COP";
    private static final long TRANSACTION_LIFETIME = 3600;

    private final PedidoRepository pedidoRepository;
    private final EmailService emailService;

    private final String merchantId;
    private final String apiKey;
    private final String accountId;
    private final String payuUrl;
    private final String confirmationUrl;
    private final String defaultResponseUrl;

    public PayUService(PedidoRepository pedidoRepository,
                       EmailService emailService,
                       @Value("${PAYU_MERCHANT_ID:}") String merchantId,
                       @Value("${PAYU_API_KEY:}") String apiKey,
                       @Value("${PAYU_ACCOUNT_ID:}") String accountId,
                       @Value("${PAYU_URL:}") String payuUrl,
                       @Value("${PAYU_CONFIRMATION_URL:/api/pagos/payu/confirmacion}") String confirmationUrl,
                       @Value("${PAYU_RESPONSE_URL:/pago/resultado}") String defaultResponseUrl) {
        this.pedidoRepository = pedidoRepository;
        this.emailService = emailService;
        this.merchantId = merchantId;
        this.apiKey = apiKey;
        this.accountId = accountId;
        this.payuUrl = payuUrl;
        this.confirmationUrl = confirmationUrl;
        this.defaultResponseUrl = defaultResponseUrl;
    }

    public boolean hayCredencialesPayU() {
        return merchantId != null && !merchantId.isBlank()
            && apiKey != null && !apiKey.isBlank()
            && accountId != null && !accountId.isBlank();
    }

    private String generarReferenceCode(Long pedidoId, String tipoPago) {
        return "PED-" + pedidoId + "-" + tipoPago + "-" + System.currentTimeMillis();
    }

    private String generarFirma(String referenceCode, BigDecimal amount) {
        try {
            String raw = apiKey + "~" + merchantId + "~" + referenceCode + "~" + amount + "~" + CURRENCY;
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(raw.getBytes("UTF-8"));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar firma PayU", e);
        }
    }

    private Map<String, String> generarParametrosForm(String referenceCode, BigDecimal amount,
                                                       String descripcion, String buyerNombre,
                                                       String buyerEmail, String responseUrl) {
        Map<String, String> params = new HashMap<>();
        params.put("merchantId", merchantId);
        params.put("accountId", accountId);
        params.put("description", descripcion);
        params.put("referenceCode", referenceCode);
        params.put("amount", amount.toString());
        params.put("tax", "0");
        params.put("taxReturnBase", "0");
        params.put("currency", CURRENCY);
        params.put("signature", generarFirma(referenceCode, amount));
        params.put("test", "1");
        params.put("buyerEmail", buyerEmail);
        params.put("buyerFullName", buyerNombre);
        params.put("responseUrl", responseUrl != null ? responseUrl : defaultResponseUrl);
        params.put("confirmationUrl", confirmationUrl);
        params.put("lifetime", String.valueOf(TRANSACTION_LIFETIME));
        return params;
    }

    private BigDecimal calcularTotalPedido(Pedido pedido) {
        return pedido.getTotalPedido() != null ? pedido.getTotalPedido() : BigDecimal.ZERO;
    }

    @Transactional
    public PayUIniciarResponse iniciarPago(Long pedidoId, String tipoPago, String responseUrl) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado: " + pedidoId));

        BigDecimal total = calcularTotalPedido(pedido);
        BigDecimal montoPago;

        if ("PRIMER_PAGO".equals(tipoPago)) {
            montoPago = total.multiply(BigDecimal.valueOf(0.5)).setScale(2, RoundingMode.HALF_UP);
        } else if ("SEGUNDO_PAGO".equals(tipoPago)) {
            montoPago = total.multiply(BigDecimal.valueOf(0.5)).setScale(2, RoundingMode.HALF_UP);
        } else {
            montoPago = total;
        }

        String referenceCode = generarReferenceCode(pedidoId, tipoPago);
        String descripcion = "Pedido #" + pedidoId + " - Flores Eternas LP";

        pedido.setPayuReferenceCode(referenceCode);
        pedido.setPayuEstado("PENDIENTE");
        pedido.setTipoPedido("PERSONALIZADO");
        pedidoRepository.save(pedido);

        PayUIniciarResponse response = new PayUIniciarResponse();
        response.setPedidoId(pedidoId);
        response.setReferenceCode(referenceCode);

        if (hayCredencialesPayU()) {
            response.setUrlPago(payuUrl);
            String buyerNombre = pedido.getCliente() != null ? pedido.getCliente().getNombreCliente() : "";
            String buyerEmail = pedido.getEmailCliente() != null ? pedido.getEmailCliente() : "";
            response.setParametrosForm(generarParametrosForm(
                    referenceCode, montoPago, descripcion,
                    buyerNombre, buyerEmail, responseUrl));
        } else {
            response.setUrlPago(null);
            response.setParametrosForm(new HashMap<>());
            response.getParametrosForm().put("modo", "simulacion");
            response.getParametrosForm().put("mensaje", "PayU no configurado. Modo simulación.");
        }

        return response;
    }

    @Transactional
    public void confirmarPago(Map<String, String> params) {
        String referenceCode = params.get("reference_sale");
        if (referenceCode == null || referenceCode.isBlank()) return;

        Optional<Pedido> optPedido = pedidoRepository.findAll().stream()
                .filter(p -> referenceCode.equals(p.getPayuReferenceCode()))
                .findFirst();

        if (optPedido.isEmpty()) {
            log.warn("Confirmacion PayU para referencia desconocida: {}", referenceCode);
            return;
        }

        Pedido pedido = optPedido.get();
        String estadoStr = params.get("state_pol");
        String transactionId = params.get("transaction_id");
        String valueStr = params.get("value");

        String estadoPayu = mapearEstadoPayU(estadoStr);
        pedido.setPayuTransactionId(transactionId);
        pedido.setPayuEstado(estadoPayu);

        if ("APROBADO".equals(estadoPayu) && valueStr != null) {
            BigDecimal montoPagado = new BigDecimal(valueStr);
            BigDecimal actualPagado = pedido.getMontoPagado() != null ? pedido.getMontoPagado() : BigDecimal.ZERO;
            pedido.setMontoPagado(actualPagado.add(montoPagado));

            if (pedido.getMontoPagado().compareTo(calcularTotalPedido(pedido)) >= 0) {
                pedido.setEstado(Estado.PENDIENTE_DE_ENTREGA);
            } else {
                pedido.setEstado(Estado.EN_PROCESO);
            }

            pedidoRepository.save(pedido);

            if (pedido.getEmailCliente() != null && !pedido.getEmailCliente().isBlank()) {
                String asunto = "Pago recibido - Pedido #" + pedido.getId();
                String cuerpo = "<h2>¡Pago recibido!</h2>"
                        + "<p>Tu pedido #" + pedido.getId() + " está en proceso.</p>"
                        + "<p>Total pagado: $" + pedido.getMontoPagado() + "</p>";
                emailService.enviarEmail(pedido.getEmailCliente(), asunto, cuerpo);
            }
        } else {
            pedidoRepository.save(pedido);
        }

        log.info("Pago pedido {} actualizado: {} -> {}", pedido.getId(), estadoStr, estadoPayu);
    }

    private String mapearEstadoPayU(String statePol) {
        if (statePol == null) return "PENDIENTE";
        return switch (statePol) {
            case "4" -> "APROBADO";
            case "6" -> "DECLINADO";
            case "5" -> "EXPIRADO";
            case "104" -> "ERROR";
            default -> "PENDIENTE";
        };
    }
}
