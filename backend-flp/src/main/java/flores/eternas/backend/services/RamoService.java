package flores.eternas.backend.services;

import flores.eternas.backend.dto.*;
import flores.eternas.backend.model.*;
import flores.eternas.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
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

    @Transactional(readOnly = true)
    public CatalogoResponse obtenerCatalogo() {
        CatalogoResponse response = new CatalogoResponse();

        Optional<CategoriaRamo> catPredefinidos = categoriaRamoRepository
                .findByDescripcionCategoriaRamoIgnoreCase("Predefinidos");
        Optional<CategoriaRamo> catTemporada = categoriaRamoRepository
                .findByDescripcionCategoriaRamoIgnoreCase("Temporada");

        List<RamoResumenDTO> predefinidos = catPredefinidos
                .map(cat -> ramoRepository.findByCategoriaRamoOrderByNombreRamoAsc(cat))
                .orElse(Collections.emptyList())
                .stream()
                .map(this::toResumenDTO)
                .collect(Collectors.toList());

        List<RamoResumenDTO> temporada = catTemporada
                .map(cat -> ramoRepository.findByCategoriaRamoOrderByNombreRamoAsc(cat))
                .orElse(Collections.emptyList())
                .stream()
                .map(this::toResumenDTO)
                .collect(Collectors.toList());

        response.setPredefinidos(predefinidos);
        response.setTemporada(temporada);

        boolean hayDisponibles = predefinidos.stream().anyMatch(RamoResumenDTO::isDisponible)
                || temporada.stream().anyMatch(RamoResumenDTO::isDisponible);

        if (!hayDisponibles) {
            response.setMensaje("No hay ramos disponibles en este momento.");
        }

        return response;
    }

    @Transactional(readOnly = true)
    public RamoDetalleDTO obtenerDetalle(Long id) {
        Ramo ramo = ramoRepository.findById(id)
                .orElse(null);
        if (ramo == null) {
            return null;
        }
        return toDetalleDTO(ramo);
    }

    @Transactional(readOnly = true)
    public List<RamoResumenDTO> listarPredefinidos() {
        return categoriaRamoRepository
                .findByDescripcionCategoriaRamoIgnoreCase("Predefinidos")
                .map(cat -> ramoRepository.findByCategoriaRamoOrderByNombreRamoAsc(cat))
                .orElse(Collections.emptyList())
                .stream()
                .map(this::toResumenDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RamoResumenDTO> listarTemporada() {
        return categoriaRamoRepository
                .findByDescripcionCategoriaRamoIgnoreCase("Temporada")
                .map(cat -> ramoRepository.findByCategoriaRamoOrderByNombreRamoAsc(cat))
                .orElse(Collections.emptyList())
                .stream()
                .map(this::toResumenDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FlorDisponibleDTO> listarFloresDisponibles() {
        List<TipoFlor> tiposFlor = tipoFlorRepository.findAllByOrderByDescripcionFlorAsc();
        List<ColorFlor> colores = colorFlorRepository.findAllByOrderByDescripcionColorAsc();

        if (tiposFlor.isEmpty() || colores.isEmpty()) {
            return Collections.emptyList();
        }

        List<FlorDisponibleDTO> flores = new ArrayList<>();
        for (TipoFlor tipo : tiposFlor) {
            for (ColorFlor color : colores) {
                flores.add(new FlorDisponibleDTO(
                        null,
                        tipo.getDescripcionFlor(),
                        color.getDescripcionColor(),
                        tipo.getPrecioUnidad()
                ));
            }
        }
        return flores;
    }

    private RamoResumenDTO toResumenDTO(Ramo ramo) {
        return new RamoResumenDTO(
                ramo.getId(),
                ramo.getNombreRamo(),
                ramo.getFotoRamo(),
                ramo.getDescripcionCorta(),
                ramo.getPrecioRamo(),
                ramo.getDisponible() != null && ramo.getDisponible()
        );
    }

    private RamoDetalleDTO toDetalleDTO(Ramo ramo) {
        RamoDetalleDTO dto = new RamoDetalleDTO();
        dto.setId(ramo.getId());
        dto.setNombre(ramo.getNombreRamo());
        dto.setFoto(ramo.getFotoRamo());
        dto.setDescripcionCorta(ramo.getDescripcionCorta());
        dto.setDescripcionCompleta(ramo.getDescripcionRamo());
        dto.setPrecio(ramo.getPrecioRamo());
        dto.setDisponible(ramo.getDisponible() != null && ramo.getDisponible());

        if (ramo.getCategoriaRamo() != null) {
            dto.setCategoriaRamo(ramo.getCategoriaRamo().getDescripcionCategoriaRamo());
        }

        if (ramo.getDetallesRamo() != null) {
            List<ComposicionRamoDTO> composicion = ramo.getDetallesRamo().stream()
                    .filter(dr -> dr.getTipoFlor() != null)
                    .map(dr -> new ComposicionRamoDTO(
                            dr.getTipoFlor().getDescripcionFlor(),
                            dr.getColorFlor() != null ? dr.getColorFlor().getDescripcionColor() : null,
                            dr.getCantidad() != null ? dr.getCantidad() : 0,
                            dr.getTipoFlor().getPrecioUnidad()
                    ))
                    .collect(Collectors.toList());
            dto.setComposicion(composicion);
        }

        return dto;
    }
}
