package flores.eternas.backend.dto;

import java.math.BigDecimal;

public class RamoResumenDTO {

    private Long id;
    private String nombre;
    private String foto;
    private String descripcionCorta;
    private BigDecimal precio;
    private boolean disponible;

    public RamoResumenDTO() {}

    public RamoResumenDTO(Long id, String nombre, String foto, String descripcionCorta, BigDecimal precio, boolean disponible) {
        this.id = id;
        this.nombre = nombre;
        this.foto = foto;
        this.descripcionCorta = descripcionCorta;
        this.precio = precio;
        this.disponible = disponible;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }
    public String getDescripcionCorta() { return descripcionCorta; }
    public void setDescripcionCorta(String descripcionCorta) { this.descripcionCorta = descripcionCorta; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
}
