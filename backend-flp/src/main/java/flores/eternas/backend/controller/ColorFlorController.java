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

import flores.eternas.backend.dto.ColorFlorDTO;
import flores.eternas.backend.services.ColorFlorService;

import java.util.List;

//Santiago Montenegro HU6
@RestController
@RequestMapping("/api/colores-flor")
public class ColorFlorController {

    private final ColorFlorService service;

    public ColorFlorController(ColorFlorService service) {
        this.service = service;
    }

    //Santiago Montenegro HU6
    @GetMapping
    public ResponseEntity<List<ColorFlorDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    //Santiago Montenegro HU6
    @GetMapping("/{id}")
    public ResponseEntity<ColorFlorDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    //Santiago Montenegro HU6
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ColorFlorDTO> crear(@RequestBody ColorFlorDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    //Santiago Montenegro HU6
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ColorFlorDTO> actualizar(@PathVariable Long id, @RequestBody ColorFlorDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    //Santiago Montenegro HU6
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
