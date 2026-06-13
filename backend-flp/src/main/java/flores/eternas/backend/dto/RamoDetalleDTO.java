package flores.eternas.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public class RamoDetalleDTO {

    private Long id;
    private String nombre;
    private String foto;
    private String descripcionCorta;
    private String descripcionCompleta;
    private BigDecimal precio;
    private boolean disponible;
    private String categoriaRamo;
    private List<ComposicionRamoDTO> composicion;

    public RamoDetalleDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }
    public String getDescripcionCorta() { return descripcionCorta; }
    public void setDescripcionCorta(String descripcionCorta) { this.descripcionCorta = descripcionCorta; }
    public String getDescripcionCompleta() { return descripcionCompleta; }
    public void setDescripcionCompleta(String descripcionCompleta) { this.descripcionCompleta = descripcionCompleta; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
    public String getCategoriaRamo() { return categoriaRamo; }
    public void setCategoriaRamo(String categoriaRamo) { this.categoriaRamo = categoriaRamo; }
    public List<ComposicionRamoDTO> getComposicion() { return composicion; }
    public void setComposicion(List<ComposicionRamoDTO> composicion) { this.composicion = composicion; }
}
