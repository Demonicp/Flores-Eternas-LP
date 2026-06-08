package flores.eternas.backend.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import flores.eternas.backend.dto.CategoriaRamoDTO;
import flores.eternas.backend.dto.ColorFlorDTO;
import flores.eternas.backend.dto.DetalleRamoLineaDTO;
import flores.eternas.backend.dto.RamoRequestDTO;
import flores.eternas.backend.dto.RamoResponseDTO;
import flores.eternas.backend.dto.TipoFlorDTO;
import flores.eternas.backend.model.CategoriaRamo;
import flores.eternas.backend.model.ColorFlor;
import flores.eternas.backend.model.DetalleRamo;
import flores.eternas.backend.model.Ramo;
import flores.eternas.backend.model.TipoFlor;
import flores.eternas.backend.repository.CategoriaRamoRepository;
import flores.eternas.backend.repository.ColorFlorRepository;
import flores.eternas.backend.repository.RamoRepository;
import flores.eternas.backend.repository.TipoFlorRepository;

import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//Santiago Montenegro HU6
@Service
@Transactional
public class RamoService {

    private final RamoRepository ramoRepository;
    private final CategoriaRamoRepository categoriaRamoRepository;
    private final TipoFlorRepository tipoFlorRepository;
    private final ColorFlorRepository colorFlorRepository;

    public RamoService(RamoRepository ramoRepository,
                       CategoriaRamoRepository categoriaRamoRepository,
                       TipoFlorRepository tipoFlorRepository,
                       ColorFlorRepository colorFlorRepository) {
        this.ramoRepository = ramoRepository;
        this.categoriaRamoRepository = categoriaRamoRepository;
        this.tipoFlorRepository = tipoFlorRepository;
        this.colorFlorRepository = colorFlorRepository;
    }

    //Santiago Montenegro HU6
    @Transactional(readOnly = true)
    public List<RamoResponseDTO> listarTodos() {
        return ramoRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    //Santiago Montenegro HU6
    @Transactional(readOnly = true)
    public RamoResponseDTO obtenerPorId(Long id) {
        Ramo ramo = ramoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ramo no encontrado con id: " + id));
        return toResponseDTO(ramo);
    }

    //Santiago Montenegro HU6
    public RamoResponseDTO crear(RamoRequestDTO dto) {
        Ramo ramo = new Ramo();
        ramo.setNombreRamo(dto.getNombreRamo());
        ramo.setPrecioRamo(dto.getPrecioRamo());
        ramo.setDescripcionRamo(dto.getDescripcionRamo());
        ramo.setFotoRamo(dto.getFotoRamo());

        CategoriaRamo categoria = categoriaRamoRepository.findById(dto.getIdCategoriaRamo())
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada con id: " + dto.getIdCategoriaRamo()));
        ramo.setCategoriaRamo(categoria);

        List<DetalleRamo> detalles = new ArrayList<>();
        if (dto.getDetallesRamo() != null) {
            for (DetalleRamoLineaDTO linea : dto.getDetallesRamo()) {
                DetalleRamo detalle = new DetalleRamo();
                detalle.setCantidad(linea.getCantidad());
                detalle.setTipoFlor(tipoFlorRepository.findById(linea.getIdTipoFlor())
                        .orElseThrow(() -> new EntityNotFoundException("Tipo de flor no encontrado con id: " + linea.getIdTipoFlor())));
                detalle.setColorFlor(colorFlorRepository.findById(linea.getIdColorFlor())
                        .orElseThrow(() -> new EntityNotFoundException("Color no encontrado con id: " + linea.getIdColorFlor())));
                detalle.setRamo(ramo);
                detalles.add(detalle);
            }
        }
        ramo.setDetallesRamo(detalles);

        Ramo guardado = ramoRepository.save(ramo);
        return toResponseDTO(guardado);
    }

    //Santiago Montenegro HU6
    public RamoResponseDTO actualizar(Long id, RamoRequestDTO dto) {
        Ramo ramo = ramoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ramo no encontrado con id: " + id));

        ramo.setNombreRamo(dto.getNombreRamo());
        ramo.setPrecioRamo(dto.getPrecioRamo());
        ramo.setDescripcionRamo(dto.getDescripcionRamo());
        ramo.setFotoRamo(dto.getFotoRamo());

        CategoriaRamo categoria = categoriaRamoRepository.findById(dto.getIdCategoriaRamo())
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada con id: " + dto.getIdCategoriaRamo()));
        ramo.setCategoriaRamo(categoria);

        ramo.getDetallesRamo().clear();
        if (dto.getDetallesRamo() != null) {
            for (DetalleRamoLineaDTO linea : dto.getDetallesRamo()) {
                DetalleRamo detalle = new DetalleRamo();
                detalle.setCantidad(linea.getCantidad());
                detalle.setTipoFlor(tipoFlorRepository.findById(linea.getIdTipoFlor())
                        .orElseThrow(() -> new EntityNotFoundException("Tipo de flor no encontrado con id: " + linea.getIdTipoFlor())));
                detalle.setColorFlor(colorFlorRepository.findById(linea.getIdColorFlor())
                        .orElseThrow(() -> new EntityNotFoundException("Color no encontrado con id: " + linea.getIdColorFlor())));
                detalle.setRamo(ramo);
                ramo.getDetallesRamo().add(detalle);
            }
        }

        Ramo guardado = ramoRepository.save(ramo);
        return toResponseDTO(guardado);
    }

    //Santiago Montenegro HU6
    public void eliminar(Long id) {
        if (!ramoRepository.existsById(id)) {
            throw new EntityNotFoundException("Ramo no encontrado con id: " + id);
        }
        ramoRepository.deleteById(id);
    }

    private RamoResponseDTO toResponseDTO(Ramo ramo) {
        RamoResponseDTO dto = new RamoResponseDTO();
        dto.setId(ramo.getId());
        dto.setNombreRamo(ramo.getNombreRamo());
        dto.setPrecioRamo(ramo.getPrecioRamo());
        dto.setDescripcionRamo(ramo.getDescripcionRamo());
        dto.setFotoRamo(ramo.getFotoRamo());

        if (ramo.getCategoriaRamo() != null) {
            dto.setCategoria(new CategoriaRamoDTO(
                    ramo.getCategoriaRamo().getId(),
                    ramo.getCategoriaRamo().getDescripcionCategoriaRamo()
            ));
        }

        if (ramo.getDetallesRamo() != null) {
            dto.setDetallesRamo(ramo.getDetallesRamo().stream().map(det -> {
                RamoResponseDTO.DetalleRamoResponseDTO detDTO = new RamoResponseDTO.DetalleRamoResponseDTO();
                detDTO.setId(det.getId());
                detDTO.setCantidad(det.getCantidad());

                if (det.getTipoFlor() != null) {
                    detDTO.setTipoFlor(new TipoFlorDTO(
                            det.getTipoFlor().getId(),
                            det.getTipoFlor().getDescripcionFlor(),
                            det.getTipoFlor().getPrecioUnidad(),
                            det.getTipoFlor().getPorcentajePorMayor()
                    ));
                }

                if (det.getColorFlor() != null) {
                    detDTO.setColorFlor(new ColorFlorDTO(
                            det.getColorFlor().getId(),
                            det.getColorFlor().getDescripcionColor()
                    ));
                }

                return detDTO;
            }).collect(Collectors.toList()));
        }

        return dto;
    }
}
