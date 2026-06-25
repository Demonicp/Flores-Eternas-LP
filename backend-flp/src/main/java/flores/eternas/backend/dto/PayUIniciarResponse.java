package flores.eternas.backend.dto;

import java.util.Map;

public class PayUIniciarResponse {

    private Long pedidoId;
    private String referenceCode;
    private String urlPago;
    private Map<String, String> parametrosForm;

    public Long getPedidoId() { return pedidoId; }
    public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }
    public String getReferenceCode() { return referenceCode; }
    public void setReferenceCode(String referenceCode) { this.referenceCode = referenceCode; }
    public String getUrlPago() { return urlPago; }
    public void setUrlPago(String urlPago) { this.urlPago = urlPago; }
    public Map<String, String> getParametrosForm() { return parametrosForm; }
    public void setParametrosForm(Map<String, String> parametrosForm) { this.parametrosForm = parametrosForm; }
}
