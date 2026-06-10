package flores.eternas.backend.dto;

import java.util.List;

public class CatalogoResponse {

    private List<RamoResumenDTO> predefinidos;
    private List<RamoResumenDTO> temporada;
    private String mensaje;

    public CatalogoResponse() {}

    public List<RamoResumenDTO> getPredefinidos() { return predefinidos; }
    public void setPredefinidos(List<RamoResumenDTO> predefinidos) { this.predefinidos = predefinidos; }
    public List<RamoResumenDTO> getTemporada() { return temporada; }
    public void setTemporada(List<RamoResumenDTO> temporada) { this.temporada = temporada; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}
