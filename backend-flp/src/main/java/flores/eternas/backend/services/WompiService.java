package flores.eternas.backend.services;

import flores.eternas.backend.dto.WompiIniciarResponse;
import flores.eternas.backend.model.Pedido;
import flores.eternas.backend.model.enums.Estado;
import flores.eternas.backend.repository.PedidoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.util.Optional;

/**
 * Servicio de integración con Wompi Web Checkout.
 * Genera referencias de pago, firmas de integridad y procesa webhooks.
 * @author demonicp
 */
@Service
public class WompiService {

    private static final Logger log = LoggerFactory.getLogger(WompiService.class);
    private static final String CURRENCY = "COP";

    private final PedidoRepository pedidoRepository;
    private final EmailService emailService;

    private final String publicKey;
    private final String integritySecret;
    private final String privateKey;
    private final String wompiUrl;

    /**
     * @param pedidoRepository repositorio de pedidos
     * @param emailService servicio de email para notificaciones
     * @param publicKey      llave pública de Wompi (WOMPI_PUBLIC_KEY)
     * @param integritySecret secreto de integridad de Wompi (WOMPI_INTEGRITY_SECRET)
     * @param privateKey     llave privada de Wompi (WOMPI_PRIVATE_KEY) para verificar webhooks
     * @param wompiUrl       URL base del API de Wompi (WOMPI_URL)
     * @author demonicp
     */
    public WompiService(PedidoRepository pedidoRepository,
                        EmailService emailService,
                        @Value("${WOMPI_PUBLIC_KEY:}") String publicKey,
                        @Value("${WOMPI_INTEGRITY_SECRET:}") String integritySecret,
                        @Value("${WOMPI_PRIVATE_KEY:}") String privateKey,
                        @Value("${WOMPI_URL:https://sandbox.wompi.co/v1}") String wompiUrl) {
        this.pedidoRepository = pedidoRepository;
        this.emailService = emailService;
        this.publicKey = publicKey;
        this.integritySecret = integritySecret;
        this.privateKey = privateKey;
        this.wompiUrl = wompiUrl;
    }

    /**
     * Verifica si las credenciales de Wompi están configuradas.
     * Si no lo están, el sistema opera en modo simulación.
     * @return true si hay credenciales configuradas
     * @author demonicp
     */
    public boolean hayCredencialesWompi() {
        return publicKey != null && !publicKey.isBlank()
            && integritySecret != null && !integritySecret.isBlank();
    }

    /**
     * Genera una referencia única para la transacción.
     * @param pedidoId ID del pedido
     * @param tipoPago tipo de pago (PRIMER_PAGO, SEGUNDO_PAGO)
     * @return referencia única
     * @author demonicp
     */
    private String generarReferencia(Long pedidoId, String tipoPago) {
        return "PED-" + pedidoId + "-" + tipoPago + "-" + System.currentTimeMillis();
    }

    /**
     * Genera la firma de integridad SHA256 requerida por Wompi.
     * Fórmula: SHA256(reference + amountInCents + currency + integritySecret)
     * @param reference referencia única de pago
     * @param amountInCents monto en centavos
     * @return hash hexadecimal SHA256
     * @author demonicp
     */
    private String generarFirma(String reference, Long amountInCents) {
        try {
            String raw = reference + amountInCents + CURRENCY + integritySecret;
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(raw.getBytes("UTF-8"));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar firma Wompi", e);
        }
    }

    /**
     * Calcula el monto del pedido en centavos según el tipo de pago.
     * PRIMER_PAGO calcula 50%; si pagoCompleto es true, calcula el 100%.
     * SEGUNDO_PAGO siempre calcula el 50% restante.
     * @param pedido pedido a calcular
     * @param tipoPago tipo de pago (PRIMER_PAGO, SEGUNDO_PAGO)
     * @param pagoCompleto si es true, cobra el 100% en lugar del 50%
     * @return monto en centavos (long)
     * @author demonicp
     */
    private Long calcularMontoEnCentavos(Pedido pedido, String tipoPago, boolean pagoCompleto) {
        BigDecimal total = pedido.getTotalPedido() != null ? pedido.getTotalPedido() : BigDecimal.ZERO;
        BigDecimal monto;
        if ("SEGUNDO_PAGO".equals(tipoPago)) {
            monto = total.multiply(BigDecimal.valueOf(0.5)).setScale(2, RoundingMode.HALF_UP);
        } else if (pagoCompleto) {
            monto = total;
        } else {
            monto = total.multiply(BigDecimal.valueOf(0.5)).setScale(2, RoundingMode.HALF_UP);
        }
        return monto.multiply(BigDecimal.valueOf(100)).longValue();
    }

    /**
     * Inicia un pago generando la referencia y firma para Wompi Web Checkout.
     * En modo simulación (sin credenciales) retorna signature=null.
     * @param pedidoId   ID del pedido
     * @param tipoPago   tipo de pago (PRIMER_PAGO, SEGUNDO_PAGO)
     * @param responseUrl URL a la que Wompi redirigirá después del pago
     * @param pagoCompleto si es true, cobra el 100% en lugar del 50%
     * @return respuesta con datos para el formulario de Wompi
     * @author demonicp
     */
    @Transactional
    public WompiIniciarResponse iniciarPago(Long pedidoId, String tipoPago, String responseUrl, boolean pagoCompleto) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado: " + pedidoId));

        String reference = generarReferencia(pedidoId, tipoPago);
        Long amountInCents = calcularMontoEnCentavos(pedido, tipoPago, pagoCompleto);

        pedido.setReferenciaTransaccion(reference);
        pedido.setEstadoTransaccion("PENDIENTE");
        pedido.setTipoPedido("PERSONALIZADO");
        pedidoRepository.save(pedido);

        WompiIniciarResponse response = new WompiIniciarResponse();
        response.setPedidoId(pedidoId);
        response.setReference(reference);
        response.setCurrency(CURRENCY);
        response.setAmountInCents(amountInCents);
        response.setRedirectUrl(responseUrl != null ? responseUrl : "/pago/resultado");

        if (hayCredencialesWompi()) {
            response.setPublicKey(publicKey);
            response.setSignature(generarFirma(reference, amountInCents));
        } else {
            response.setPublicKey(null);
            response.setSignature(null);
        }

        return response;
    }

    /**
     * Verifica la firma HMAC-SHA256 del webhook usando WOMPI_PRIVATE_KEY.
     * Wompi envía el header X-Signature con formato "sha256=<hex>".
     * @param signatureHeader valor del header X-Signature
     * @param rawBody cuerpo crudo del webhook (JSON)
     * @return true si la firma es válida
     * @author demonicp
     */
    public boolean validarFirmaWebhook(String signatureHeader, String rawBody) {
        if (signatureHeader == null || signatureHeader.isBlank() || rawBody == null || rawBody.isBlank()) {
            log.warn("Webhook recibido sin firma o cuerpo");
            return false;
        }
        if (privateKey == null || privateKey.isBlank()) {
            log.warn("WOMPI_PRIVATE_KEY no configurada — no se puede verificar firma");
            return false;
        }
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(privateKey.getBytes("UTF-8"), "HmacSHA256");
            mac.init(secretKey);
            byte[] hmacBytes = mac.doFinal(rawBody.getBytes("UTF-8"));
            StringBuilder hex = new StringBuilder();
            for (byte b : hmacBytes) {
                hex.append(String.format("%02x", b));
            }
            String expected = "sha256=" + hex.toString();
            boolean valida = expected.equals(signatureHeader);
            if (!valida) {
                log.warn("Firma webhook inválida. Esperada: {}, Recibida: {}", expected, signatureHeader);
            }
            return valida;
        } catch (Exception e) {
            log.error("Error verificando firma webhook", e);
            return false;
        }
    }

    /**
     * Procesa el webhook enviado por Wompi cuando una transacción cambia de estado.
     * Verifica la firma de integridad, actualiza el pedido y envía email de confirmación.
     * @param transaction objeto con datos de la transacción según el webhook de Wompi
     * @author demonicp
     */
    @Transactional
    public void procesarWebhook(WompiTransaction transaction) {
        if (transaction == null || transaction.getReference() == null) return;

        String reference = transaction.getReference();
        Optional<Pedido> optPedido = pedidoRepository.findAll().stream()
                .filter(p -> reference.equals(p.getReferenciaTransaccion()))
                .findFirst();

        if (optPedido.isEmpty()) {
            log.warn("Webhook Wompi para referencia desconocida: {}", reference);
            return;
        }

        Pedido pedido = optPedido.get();
        String status = transaction.getStatus();
        String transactionId = transaction.getId();

        pedido.setIdTransaccion(transactionId);
        pedido.setEstadoTransaccion(status);

        if ("APPROVED".equals(status) && transaction.getAmountInCents() != null) {
            BigDecimal montoPagado = BigDecimal.valueOf(transaction.getAmountInCents())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal actualPagado = pedido.getMontoPagado() != null ? pedido.getMontoPagado() : BigDecimal.ZERO;
            pedido.setMontoPagado(actualPagado.add(montoPagado));

            if (pedido.getMontoPagado().compareTo(
                    pedido.getTotalPedido() != null ? pedido.getTotalPedido() : BigDecimal.ZERO) >= 0) {
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

        log.info("Pago pedido {} actualizado: {} -> {}", pedido.getId(), transaction.getId(), status);
    }

    /**
     * Clase interna que representa la estructura de una transacción recibida en el webhook de Wompi.
     * @author demonicp
     */
    public static class WompiTransaction {
        private String id;
        private String reference;
        private String status;
        private Long amountInCents;
        private String paymentMethodType;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getReference() { return reference; }
        public void setReference(String reference) { this.reference = reference; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public Long getAmountInCents() { return amountInCents; }
        public void setAmountInCents(Long amountInCents) { this.amountInCents = amountInCents; }
        public String getPaymentMethodType() { return paymentMethodType; }
        public void setPaymentMethodType(String paymentMethodType) { this.paymentMethodType = paymentMethodType; }
    }
}
