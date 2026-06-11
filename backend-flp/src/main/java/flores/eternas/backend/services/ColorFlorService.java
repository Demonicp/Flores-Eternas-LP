package flores.eternas.backend.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import flores.eternas.backend.dto.ColorFlorDTO;
import flores.eternas.backend.model.ColorFlor;
import flores.eternas.backend.repository.ColorFlorRepository;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

//Santiago Montenegro HU6
@Service
@Transactional
public class ColorFlorService {

    private final ColorFlorRepository repository;

    public ColorFlorService(ColorFlorRepository repository) {
        this.repository = repository;
    }

    //Santiago Montenegro HU6
    @Transactional(readOnly = true)
    public List<ColorFlorDTO> listarTodos() {
        return repository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    //Santiago Montenegro HU6
    @Transactional(readOnly = true)
    public ColorFlorDTO obtenerPorId(Long id) {
        ColorFlor entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Color no encontrado con id: " + id));
        return toDTO(entity);
    }

    //Santiago Montenegro HU6
    public ColorFlorDTO crear(ColorFlorDTO dto) {
        ColorFlor entity = new ColorFlor();
        entity.setDescripcionColor(dto.getDescripcionColor());
        ColorFlor guardado = repository.save(entity);
        return toDTO(guardado);
    }

    //Santiago Montenegro HU6
    public ColorFlorDTO actualizar(Long id, ColorFlorDTO dto) {
        ColorFlor entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Color no encontrado con id: " + id));
        entity.setDescripcionColor(dto.getDescripcionColor());
        ColorFlor guardado = repository.save(entity);
        return toDTO(guardado);
    }

    //Santiago Montenegro HU6
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Color no encontrado con id: " + id);
        }
        repository.deleteById(id);
    }

    private ColorFlorDTO toDTO(ColorFlor entity) {
        return new ColorFlorDTO(entity.getId(), entity.getDescripcionColor());
    }
}
