package flores.eternas.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import flores.eternas.backend.dto.RamoRequestDTO;
import flores.eternas.backend.dto.RamoResponseDTO;
import flores.eternas.backend.services.RamoService;

import java.util.List;

//Santiago Montenegro HU6
@RestController
@RequestMapping("/api/ramos")
public class RamoController {

    private final RamoService ramoService;

    public RamoController(RamoService ramoService) {
        this.ramoService = ramoService;
    }

    //Santiago Montenegro HU6
    @GetMapping
    public ResponseEntity<List<RamoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(ramoService.listarTodos());
    }

    //Santiago Montenegro HU6
    @GetMapping("/{id}")
    public ResponseEntity<RamoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ramoService.obtenerPorId(id));
    }

    //Santiago Montenegro HU6
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RamoResponseDTO> crear(@RequestBody RamoRequestDTO dto) {
        RamoResponseDTO response = ramoService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //Santiago Montenegro HU6
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RamoResponseDTO> actualizar(@PathVariable Long id, @RequestBody RamoRequestDTO dto) {
        return ResponseEntity.ok(ramoService.actualizar(id, dto));
    }

    //Santiago Montenegro HU6
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        ramoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
