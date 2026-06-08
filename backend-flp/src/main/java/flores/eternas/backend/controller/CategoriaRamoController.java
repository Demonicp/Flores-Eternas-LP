package flores.eternas.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import flores.eternas.backend.dto.CategoriaRamoDTO;
import flores.eternas.backend.services.CategoriaRamoService;

import java.util.List;

//Santiago Montenegro HU6
@RestController
@RequestMapping("/api/categorias-ramo")
public class CategoriaRamoController {

    private final CategoriaRamoService service;

    public CategoriaRamoController(CategoriaRamoService service) {
        this.service = service;
    }

    //Santiago Montenegro HU6
    @GetMapping
    public ResponseEntity<List<CategoriaRamoDTO>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    //Santiago Montenegro HU6
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaRamoDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    //Santiago Montenegro HU6
    @PostMapping
    public ResponseEntity<CategoriaRamoDTO> crear(@RequestBody CategoriaRamoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    //Santiago Montenegro HU6
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaRamoDTO> actualizar(@PathVariable Long id, @RequestBody CategoriaRamoDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    //Santiago Montenegro HU6
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
