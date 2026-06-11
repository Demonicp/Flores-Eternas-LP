package flores.eternas.backend.dto;

import java.math.BigDecimal;

public class ComposicionRamoDTO {

    private String tipoFlor;
    private String color;
    private Integer cantidad;
    private BigDecimal precioUnidad;

    public ComposicionRamoDTO() {}

    public ComposicionRamoDTO(String tipoFlor, String color, Integer cantidad, BigDecimal precioUnidad) {
        this.tipoFlor = tipoFlor;
        this.color = color;
        this.cantidad = cantidad;
        this.precioUnidad = precioUnidad;
    }

    public String getTipoFlor() { return tipoFlor; }
    public void setTipoFlor(String tipoFlor) { this.tipoFlor = tipoFlor; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public BigDecimal getPrecioUnidad() { return precioUnidad; }
    public void setPrecioUnidad(BigDecimal precioUnidad) { this.precioUnidad = precioUnidad; }
}
