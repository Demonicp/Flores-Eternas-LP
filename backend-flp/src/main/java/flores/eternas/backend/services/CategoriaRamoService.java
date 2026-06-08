package flores.eternas.backend.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import flores.eternas.backend.dto.CategoriaRamoDTO;
import flores.eternas.backend.model.CategoriaRamo;
import flores.eternas.backend.repository.CategoriaRamoRepository;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

//Santiago Montenegro HU6
@Service
@Transactional
public class CategoriaRamoService {

    private final CategoriaRamoRepository repository;

    public CategoriaRamoService(CategoriaRamoRepository repository) {
        this.repository = repository;
    }

    //Santiago Montenegro HU6
    @Transactional(readOnly = true)
    public List<CategoriaRamoDTO> listarTodas() {
        return repository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    //Santiago Montenegro HU6
    @Transactional(readOnly = true)
    public CategoriaRamoDTO obtenerPorId(Long id) {
        CategoriaRamo entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada con id: " + id));
        return toDTO(entity);
    }

    //Santiago Montenegro HU6
    public CategoriaRamoDTO crear(CategoriaRamoDTO dto) {
        CategoriaRamo entity = new CategoriaRamo();
        entity.setDescripcionCategoriaRamo(dto.getDescripcionCategoriaRamo());
        CategoriaRamo guardado = repository.save(entity);
        return toDTO(guardado);
    }

    //Santiago Montenegro HU6
    public CategoriaRamoDTO actualizar(Long id, CategoriaRamoDTO dto) {
        CategoriaRamo entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada con id: " + id));
        entity.setDescripcionCategoriaRamo(dto.getDescripcionCategoriaRamo());
        CategoriaRamo guardado = repository.save(entity);
        return toDTO(guardado);
    }

    //Santiago Montenegro HU6
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Categoría no encontrada con id: " + id);
        }
        repository.deleteById(id);
    }

    private CategoriaRamoDTO toDTO(CategoriaRamo entity) {
        return new CategoriaRamoDTO(entity.getId(), entity.getDescripcionCategoriaRamo());
    }
}
