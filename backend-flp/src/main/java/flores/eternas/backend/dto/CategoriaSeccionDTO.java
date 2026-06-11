package flores.eternas.backend.dto;

import java.util.List;

public class CategoriaSeccionDTO {

    private String nombre;
    private List<RamoResumenDTO> ramos;
    private String badge;

    public CategoriaSeccionDTO() {}

    public CategoriaSeccionDTO(String nombre, List<RamoResumenDTO> ramos, String badge) {
        this.nombre = nombre;
        this.ramos = ramos;
        this.badge = badge;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public List<RamoResumenDTO> getRamos() { return ramos; }
    public void setRamos(List<RamoResumenDTO> ramos) { this.ramos = ramos; }
    public String getBadge() { return badge; }
    public void setBadge(String badge) { this.badge = badge; }
}
