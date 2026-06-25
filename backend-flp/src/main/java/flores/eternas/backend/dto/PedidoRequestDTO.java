package flores.eternas.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PedidoRequestDTO {

    @NotBlank(message = "Nombre obligatorio")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "Nombre solo admite letras y espacios")
    @Size(max = 100, message = "Nombre demasiado largo")
    private String nombreCliente;

    @NotBlank(message = "Email obligatorio")
    @Email(message = "Email inválido")
    private String emailCliente;

    @NotBlank(message = "Dirección obligatoria")
    @Pattern(regexp = "^(calle|carrera|av\\.?|avenida|transversal|diagonal|circular)\\s+\\d{1,3}\\s*#\\s*\\d{1,3}-\\d{1,3}$",
             flags = Pattern.Flag.CASE_INSENSITIVE,
             message = "Dirección debe ser: calle 28 #25-38")
    private String direccionEntrega;

    @FutureOrPresent(message = "Fecha no puede ser anterior a hoy")
    private LocalDate fechaEntrega;

    private String tipoPedido;
    private String tipoPago;
    private List<ItemPedidoDTO> items;
    private List<CrearPedidoRequest.ItemFlorRequest> floresPersonalizadas;
    private List<AdicionRequest> adicionesPersonalizadas;

    @Pattern(regexp = "^\\d{6,10}$", message = "Cédula: solo números (6-10 dígitos)")
    private String cedulaCliente;

    @Pattern(regexp = "^(\\+57\\s?)?(3\\d{9}|60\\d{8})$", message = "Teléfono: +57 3001234567 o 6012345678")
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

    public static class AdicionRequest {
        private Long inventarioId;
        private Integer cantidad;

        public Long getInventarioId() { return inventarioId; }
        public void setInventarioId(Long inventarioId) { this.inventarioId = inventarioId; }
        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    }
}