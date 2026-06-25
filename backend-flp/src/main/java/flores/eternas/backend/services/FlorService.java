package flores.eternas.backend.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import flores.eternas.backend.dto.ColorFlorDTO;
import flores.eternas.backend.dto.InventarioDTO;
import flores.eternas.backend.dto.TipoFlorDTO;
import flores.eternas.backend.model.enums.Estado;
import flores.eternas.backend.repository.ColorFlorRepository;
import flores.eternas.backend.repository.InventarioRepository;
import flores.eternas.backend.repository.TipoFlorRepository;

@Service
public class FlorService {

    private final TipoFlorRepository tipoFlorRepository;
    private final ColorFlorRepository colorFlorRepository;
    private final InventarioRepository inventarioRepository;

    public FlorService(
            TipoFlorRepository tipoFlorRepository,
            ColorFlorRepository colorFlorRepository,
            InventarioRepository inventarioRepository) {
        this.tipoFlorRepository = tipoFlorRepository;
        this.colorFlorRepository = colorFlorRepository;
        this.inventarioRepository = inventarioRepository;
    }

    public List<TipoFlorDTO> obtenerTiposFlor() {
        return tipoFlorRepository.findAll().stream()
                .map(flor -> new TipoFlorDTO(flor.getId(), flor.getDescripcionFlor(), flor.getPrecioUnidad(), flor.getPorcentajePorMayor(), flor.getIcono(), flor.getIconoColor()))
                .collect(Collectors.toList());
    }

    public List<ColorFlorDTO> obtenerColoresFlor() {
        return colorFlorRepository.findAll().stream()
                .map(color -> new ColorFlorDTO(color.getId(), color.getDescripcionColor()))
                .collect(Collectors.toList());
    }

    public List<InventarioDTO> obtenerAdicionesDisponibles() {
        return inventarioRepository.findByEstado(Estado.DISPONIBLE).stream()
                .map(item -> new InventarioDTO(
                        item.getId(),
                        item.getNombreInventario(),
                        item.getDescripcionInventario(),
                        item.getPrecioCosto(),
                        item.getStock()))
                .collect(Collectors.toList());
    }
}
