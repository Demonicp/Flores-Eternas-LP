package flores.eternas.backend.dto;

/**
 * DTO de respuesta para iniciar un pago con Wompi Web Checkout.
 * El frontend usa estos datos para construir el formulario POST a Wompi.
 * @author demonicp
 */
public class WompiIniciarResponse {

    private Long pedidoId;
    private String publicKey;
    private String currency;
    private Long amountInCents;
    private String reference;
    private String signature;
    private String redirectUrl;

    public Long getPedidoId() { return pedidoId; }
    public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }
    public String getPublicKey() { return publicKey; }
    public void setPublicKey(String publicKey) { this.publicKey = publicKey; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public Long getAmountInCents() { return amountInCents; }
    public void setAmountInCents(Long amountInCents) { this.amountInCents = amountInCents; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }
    public String getRedirectUrl() { return redirectUrl; }
    public void setRedirectUrl(String redirectUrl) { this.redirectUrl = redirectUrl; }
}
