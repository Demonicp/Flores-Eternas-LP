package flores.eternas.backend.controller;

import flores.eternas.backend.dto.PedidoResponseDTO;
import flores.eternas.backend.services.PedidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/pedidos")
public class AdminPedidoController {

    private final PedidoService pedidoService;

    public AdminPedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PedidoResponseDTO>> listarPedidos() {
        return ResponseEntity.ok(pedidoService.listarPedidos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PedidoResponseDTO> obtenerPedido(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.obtenerPedido(id));
    }

    @PutMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> actualizarEstado(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        try {
            String nuevoEstado = body.get("estado");
            if (nuevoEstado == null || nuevoEstado.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "El campo 'estado' es requerido"));
            }
            PedidoResponseDTO response = pedidoService.actualizarEstadoPedido(id, nuevoEstado);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
