package flores.eternas.backend.services;

import flores.eternas.backend.dto.InventarioDTO;
import flores.eternas.backend.model.Inventario;
import flores.eternas.backend.repository.InventarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InventarioService {

    private final InventarioRepository repository;

    public InventarioService(InventarioRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<InventarioDTO> listarTodos() {
        return repository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InventarioDTO obtenerPorId(Long id) {
        Inventario inventario = repository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Inventario no encontrado"));

        return toDTO(inventario);
    }

    public InventarioDTO crear(InventarioDTO dto) {
        Inventario entity = new Inventario();

        entity.setNombreInventario(dto.getNombre());
        entity.setDescripcionInventario(dto.getDescripcion());
        entity.setPrecioCosto(dto.getPrecioCosto());
        entity.setStock(dto.getStock());

        Inventario guardado = repository.save(entity);
        return toDTO(guardado);
    }

    public InventarioDTO actualizar(Long id, InventarioDTO dto) {
        Inventario entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Inventario no encontrado con id: " + id));

        entity.setNombreInventario(dto.getNombre());
        entity.setDescripcionInventario(dto.getDescripcion());
        entity.setPrecioCosto(dto.getPrecioCosto());
        entity.setStock(dto.getStock());
            
        Inventario guardado = repository.save(entity);
        return toDTO(guardado);
    }

    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Inventario no encontrado con id: " + id);
        }
        repository.deleteById(id);
    }

    private InventarioDTO toDTO(Inventario inventario) {
        return new InventarioDTO(
                inventario.getId(),
                inventario.getNombreInventario(),
                inventario.getDescripcionInventario(),
                inventario.getPrecioCosto(),
                inventario.getStock()
        );
    }
}