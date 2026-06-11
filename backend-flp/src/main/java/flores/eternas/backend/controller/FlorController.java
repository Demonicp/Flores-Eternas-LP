package flores.eternas.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import flores.eternas.backend.dto.ColorFlorDTO;
import flores.eternas.backend.dto.InventarioDTO;
import flores.eternas.backend.dto.TipoFlorDTO;
import flores.eternas.backend.services.FlorService;

@RestController
@RequestMapping("/api/flores")
public class FlorController {

    private final FlorService florService;

    public FlorController(FlorService florService) {
        this.florService = florService;
    }

    @GetMapping("/tipos")
    public ResponseEntity<List<TipoFlorDTO>> obtenerTiposFlor() {
        return ResponseEntity.ok(florService.obtenerTiposFlor());
    }

    @GetMapping("/colores")
    public ResponseEntity<List<ColorFlorDTO>> obtenerColoresFlor() {
        return ResponseEntity.ok(florService.obtenerColoresFlor());
    }

    @GetMapping("/adiciones")
    public ResponseEntity<List<InventarioDTO>> obtenerAdiciones() {
        return ResponseEntity.ok(florService.obtenerAdicionesDisponibles());
    }
}
