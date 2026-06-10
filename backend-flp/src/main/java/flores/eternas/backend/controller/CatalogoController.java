package flores.eternas.backend.controller;

import flores.eternas.backend.dto.*;
import flores.eternas.backend.services.RamoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogo")
public class CatalogoController {

    private final RamoService ramoService;

    public CatalogoController(RamoService ramoService) {
        this.ramoService = ramoService;
    }

    @GetMapping
    public ResponseEntity<CatalogoResponse> obtenerCatalogo() {
        CatalogoResponse catalogo = ramoService.obtenerCatalogo();
        return ResponseEntity.ok(catalogo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerDetalle(@PathVariable Long id) {
        RamoDetalleDTO detalle = ramoService.obtenerDetalle(id);
        if (detalle == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(detalle);
    }

    @GetMapping("/predefinidos")
    public ResponseEntity<List<RamoResumenDTO>> listarPredefinidos() {
        List<RamoResumenDTO> ramos = ramoService.listarPredefinidos();
        return ResponseEntity.ok(ramos);
    }

    @GetMapping("/temporada")
    public ResponseEntity<List<RamoResumenDTO>> listarTemporada() {
        List<RamoResumenDTO> ramos = ramoService.listarTemporada();
        return ResponseEntity.ok(ramos);
    }

    @GetMapping("/flores-disponibles")
    public ResponseEntity<List<FlorDisponibleDTO>> listarFloresDisponibles() {
        List<FlorDisponibleDTO> flores = ramoService.listarFloresDisponibles();
        return ResponseEntity.ok(flores);
    }

    @PostMapping("/seed")
    public ResponseEntity<String> seedCatalogo() {
        ramoService.seedCatalogo();
        return ResponseEntity.ok("Catálogo poblado con 10 ramos por sección");
    }
}
