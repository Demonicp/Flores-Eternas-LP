package flores.eternas.backend.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import flores.eternas.backend.dto.TipoFlorDTO;
import flores.eternas.backend.model.TipoFlor;
import flores.eternas.backend.repository.TipoFlorRepository;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

//Santiago Montenegro HU6
@Service
@Transactional
public class TipoFlorService {

    private final TipoFlorRepository repository;

    public TipoFlorService(TipoFlorRepository repository) {
        this.repository = repository;
    }

    //Santiago Montenegro HU6
    @Transactional(readOnly = true)
    public List<TipoFlorDTO> listarTodos() {
        return repository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    //Santiago Montenegro HU6
    @Transactional(readOnly = true)
    public TipoFlorDTO obtenerPorId(Long id) {
        TipoFlor entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de flor no encontrada con id: " + id));
        return toDTO(entity);
    }

    //Santiago Montenegro HU6
    public TipoFlorDTO crear(TipoFlorDTO dto) {
        TipoFlor entity = new TipoFlor();
        entity.setDescripcionFlor(dto.getDescripcionFlor());
        entity.setPrecioUnidad(dto.getPrecioUnidad());
        entity.setPorcentajePorMayor(dto.getPorcentajePorMayor());
        entity.setIcono(dto.getIcono());
        TipoFlor guardado = repository.save(entity);
        return toDTO(guardado);
    }

    //Santiago Montenegro HU6
    public TipoFlorDTO actualizar(Long id, TipoFlorDTO dto) {
        TipoFlor entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de flor no encontrada con id: " + id));
        entity.setDescripcionFlor(dto.getDescripcionFlor());
        entity.setPrecioUnidad(dto.getPrecioUnidad());
        entity.setPorcentajePorMayor(dto.getPorcentajePorMayor());
        entity.setIcono(dto.getIcono());
        TipoFlor guardado = repository.save(entity);
        return toDTO(guardado);
    }

    //Santiago Montenegro HU6
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Tipo de flor no encontrada con id: " + id);
        }
        repository.deleteById(id);
    }

    private TipoFlorDTO toDTO(TipoFlor entity) {
        return new TipoFlorDTO(
                entity.getId(),
                entity.getDescripcionFlor(),
                entity.getPrecioUnidad(),
                entity.getPorcentajePorMayor(),
                entity.getIcono()
        );
    }
}
