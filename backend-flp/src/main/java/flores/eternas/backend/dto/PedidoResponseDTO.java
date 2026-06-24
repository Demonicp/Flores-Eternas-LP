package flores.eternas.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PedidoResponseDTO {

    private Long id;
    private BigDecimal total;
    private BigDecimal montoPagado;
    private BigDecimal montoPendiente;
    private String estado;
    private String tipoPedido;
    private LocalDate fechaEntrega;
    private String mensaje;
    private String nombreCliente;
    private String emailCliente;
    private String direccionEntrega;
    private List<ItemPedidoDTO> items;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public BigDecimal getMontoPagado() { return montoPagado; }
    public void setMontoPagado(BigDecimal montoPagado) { this.montoPagado = montoPagado; }
    public BigDecimal getMontoPendiente() { return montoPendiente; }
    public void setMontoPendiente(BigDecimal montoPendiente) { this.montoPendiente = montoPendiente; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getTipoPedido() { return tipoPedido; }
    public void setTipoPedido(String tipoPedido) { this.tipoPedido = tipoPedido; }
    public LocalDate getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(LocalDate fechaEntrega) { this.fechaEntrega = fechaEntrega; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
    public String getEmailCliente() { return emailCliente; }
    public void setEmailCliente(String emailCliente) { this.emailCliente = emailCliente; }
    public String getDireccionEntrega() { return direccionEntrega; }
    public void setDireccionEntrega(String direccionEntrega) { this.direccionEntrega = direccionEntrega; }
    public List<ItemPedidoDTO> getItems() { return items; }
    public void setItems(List<ItemPedidoDTO> items) { this.items = items; }

    public static class ItemPedidoDTO {
        private String nombreRamo;
        private Integer cantidad;
        private BigDecimal precioUnitario;

        public String getNombreRamo() { return nombreRamo; }
        public void setNombreRamo(String nombreRamo) { this.nombreRamo = nombreRamo; }
        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
        public BigDecimal getPrecioUnitario() { return precioUnitario; }
        public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    }
}
