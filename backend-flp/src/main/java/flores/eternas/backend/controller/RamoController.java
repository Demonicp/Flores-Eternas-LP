package flores.eternas.backend.controller;

import flores.eternas.backend.dto.RamoRequestDTO;
import flores.eternas.backend.dto.RamoResponseDTO;
import flores.eternas.backend.services.RamoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ramos")
public class RamoController {

    private final RamoService ramoService;

    public RamoController(RamoService ramoService) {
        this.ramoService = ramoService;
    }

    @GetMapping
    public ResponseEntity<List<RamoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(ramoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RamoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ramoService.obtenerPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RamoResponseDTO> crear(@RequestBody RamoRequestDTO dto) {
        RamoResponseDTO response = ramoService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RamoResponseDTO> actualizar(@PathVariable Long id, @RequestBody RamoRequestDTO dto) {
        return ResponseEntity.ok(ramoService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        ramoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
