package flores.eternas.backend.dto;

import java.util.List;

public class CatalogoResponse {

    private List<RamoResumenDTO> predefinidos;
    private List<RamoResumenDTO> temporada;
    private List<CategoriaSeccionDTO> secciones;
    private String mensaje;

    public CatalogoResponse() {}

    public List<RamoResumenDTO> getPredefinidos() { return predefinidos; }
    public void setPredefinidos(List<RamoResumenDTO> predefinidos) { this.predefinidos = predefinidos; }
    public List<RamoResumenDTO> getTemporada() { return temporada; }
    public void setTemporada(List<RamoResumenDTO> temporada) { this.temporada = temporada; }
    public List<CategoriaSeccionDTO> getSecciones() { return secciones; }
    public void setSecciones(List<CategoriaSeccionDTO> secciones) { this.secciones = secciones; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}
