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

    public static class ItemPedidoDTO {
        private Long idRamo;
        private Integer cantidad;

        public Long getIdRamo() { return idRamo; }
        public void setIdRamo(Long idRamo) { this.idRamo = idRamo; }
        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    }
}
