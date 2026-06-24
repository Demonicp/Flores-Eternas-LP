package flores.eternas.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PedidoRequestDTO {

    private String nombreCliente;
    private String emailCliente;
    private String direccionEntrega;
    private LocalDate fechaEntrega;
    private String tipoPedido;
    private String tipoPago;
    private List<ItemPedidoDTO> items;
    private List<CrearPedidoRequest.ItemFlorRequest> floresPersonalizadas;
    private List<AdicionRequest> adicionesPersonalizadas;
    private String cedulaCliente;
    private String telefonoCliente;

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
    public String getEmailCliente() { return emailCliente; }
    public void setEmailCliente(String emailCliente) { this.emailCliente = emailCliente; }
    public String getDireccionEntrega() { return direccionEntrega; }
    public void setDireccionEntrega(String direccionEntrega) { this.direccionEntrega = direccionEntrega; }
    public LocalDate getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(LocalDate fechaEntrega) { this.fechaEntrega = fechaEntrega; }
    public String getTipoPedido() { return tipoPedido; }
    public void setTipoPedido(String tipoPedido) { this.tipoPedido = tipoPedido; }
    public String getTipoPago() { return tipoPago; }
    public void setTipoPago(String tipoPago) { this.tipoPago = tipoPago; }
    public List<ItemPedidoDTO> getItems() { return items; }
    public void setItems(List<ItemPedidoDTO> items) { this.items = items; }
    public List<CrearPedidoRequest.ItemFlorRequest> getFloresPersonalizadas() { return floresPersonalizadas; }
    public void setFloresPersonalizadas(List<CrearPedidoRequest.ItemFlorRequest> floresPersonalizadas) { this.floresPersonalizadas = floresPersonalizadas; }
    public List<AdicionRequest> getAdicionesPersonalizadas() { return adicionesPersonalizadas; }
    public void setAdicionesPersonalizadas(List<AdicionRequest> adicionesPersonalizadas) { this.adicionesPersonalizadas = adicionesPersonalizadas; }
    public String getCedulaCliente() { return cedulaCliente; }
    public void setCedulaCliente(String cedulaCliente) { this.cedulaCliente = cedulaCliente; }
    public String getTelefonoCliente() { return telefonoCliente; }
    public void setTelefonoCliente(String telefonoCliente) { this.telefonoCliente = telefonoCliente; }

    public static class ItemPedidoDTO {
        private Long idRamo;
        private Integer cantidad;

        public Long getIdRamo() { return idRamo; }
        public void setIdRamo(Long idRamo) { this.idRamo = idRamo; }
        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    }
}
