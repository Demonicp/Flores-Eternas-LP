package flores.eternas.backend.dto;

import java.math.BigDecimal;

public class FlorDisponibleDTO {

    private Long id;
    private String tipoFlor;
    private String colorFlor;
    private BigDecimal precioUnidad;

    public FlorDisponibleDTO() {}

    public FlorDisponibleDTO(Long id, String tipoFlor, String colorFlor, BigDecimal precioUnidad) {
        this.id = id;
        this.tipoFlor = tipoFlor;
        this.colorFlor = colorFlor;
        this.precioUnidad = precioUnidad;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTipoFlor() { return tipoFlor; }
    public void setTipoFlor(String tipoFlor) { this.tipoFlor = tipoFlor; }
    public String getColorFlor() { return colorFlor; }
    public void setColorFlor(String colorFlor) { this.colorFlor = colorFlor; }
    public BigDecimal getPrecioUnidad() { return precioUnidad; }
    public void setPrecioUnidad(BigDecimal precioUnidad) { this.precioUnidad = precioUnidad; }
}
