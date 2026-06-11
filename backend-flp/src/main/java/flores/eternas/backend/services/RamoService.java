package flores.eternas.backend.services;

import flores.eternas.backend.dto.*;
import flores.eternas.backend.model.*;
import flores.eternas.backend.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

        List<CategoriaRamo> todasLasCategorias = categoriaRamoRepository.findAll();
        List<CategoriaSeccionDTO> secciones = new ArrayList<>();
        for (CategoriaRamo cat : todasLasCategorias) {
            List<RamoResumenDTO> ramos = ramoRepository
                    .findByCategoriaRamoOrderByNombreRamoAsc(cat)
                    .stream()
                    .map(this::toResumenDTO)
                    .collect(Collectors.toList());
            if (!ramos.isEmpty()) {
                String badge = determinarBadge(cat.getDescripcionCategoriaRamo());
                secciones.add(new CategoriaSeccionDTO(cat.getDescripcionCategoriaRamo(), ramos, badge));
            }
        }
        response.setSecciones(secciones);

        boolean hayDisponibles = predefinidos.stream().anyMatch(RamoResumenDTO::isDisponible)
                || temporada.stream().anyMatch(RamoResumenDTO::isDisponible);

        if (!hayDisponibles) {
            response.setMensaje("No hay ramos disponibles en este momento.");
        }

        return response;
    }

    private String determinarBadge(String nombreCategoria) {
        if (nombreCategoria == null) return null;
        String lower = nombreCategoria.toLowerCase();
        if (lower.contains("predefinidos")) {
            return "entrega-inmediata";
        }
        if (lower.contains("temporada") || lower.contains("san valentin")) {
            return "nuevo";
        }
        return null;
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

    @Transactional(readOnly = true)
    public List<RamoResponseDTO> listarTodos() {
        return ramoRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RamoResponseDTO obtenerPorId(Long id) {
        Ramo ramo = ramoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ramo no encontrado con id: " + id));
        return toResponseDTO(ramo);
    }

    public RamoResponseDTO crear(RamoRequestDTO dto) {
        Ramo ramo = new Ramo();
        ramo.setNombreRamo(dto.getNombreRamo());
        ramo.setPrecioRamo(dto.getPrecioRamo());
        ramo.setDescripcionRamo(dto.getDescripcionRamo());
        ramo.setFotoRamo(dto.getFotoRamo());
        ramo.setFechaCreacion(java.time.LocalDateTime.now());

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

    public void eliminar(Long id) {
        if (!ramoRepository.existsById(id)) {
            throw new EntityNotFoundException("Ramo no encontrado con id: " + id);
        }
        ramoRepository.deleteById(id);
    }

    private RamoResumenDTO toResumenDTO(Ramo ramo) {
        String foto = ramo.getFotoRamo();
        if (foto != null && foto.contains("cloudinary.com/demo")) {
            String[] flowerImages = {
                "https://images.unsplash.com/photo-1566378246598-5b11a0d486cc?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1490750967868-88aa4f44baee?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1526047932273-341f2a7631f9?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1582794543139-8ac9cb0f7b11?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1468327768560-75b778cbb551?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1487530811176-3780de880c2d?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1535842802993-71e182ad4f23?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1519378058457-4c29c926d6e8?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1559561853-084c0b1b6b88?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1561181286-d3fee7d55364?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1596815064285-7ed0c2216b5a?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1589123053640-8fd29e4a2c4e?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1549480017-d76466a0b2d0?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1594671146480-dc26e3086c4f?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1563241527-3004b7be0ffd?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1596436889106-be35e843f974?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1578956520118-613e9e8d45f1?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1567696153792-87f41e9b8823?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1559561853-084c0b1b6b88?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1470509037661-1e2d5e5f8b5f?w=400&h=300&fit=crop"
            };
            int idx = (int) (ramo.getId() % flowerImages.length);
            foto = flowerImages[idx];
        }
        return new RamoResumenDTO(
                ramo.getId(),
                ramo.getNombreRamo(),
                foto,
                ramo.getDescripcionCorta(),
                ramo.getPrecioRamo(),
                ramo.getDisponible() != null && ramo.getDisponible(),
                ramo.getFechaCreacion()
        );
    }

    private RamoDetalleDTO toDetalleDTO(Ramo ramo) {
        RamoDetalleDTO dto = new RamoDetalleDTO();
        dto.setId(ramo.getId());
        dto.setNombre(ramo.getNombreRamo());

        String foto = ramo.getFotoRamo();
        if (foto != null && foto.contains("cloudinary.com/demo")) {
            String[] flowerImages = {
                "https://images.unsplash.com/photo-1566378246598-5b11a0d486cc?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1490750967868-88aa4f44baee?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1526047932273-341f2a7631f9?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1582794543139-8ac9cb0f7b11?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1468327768560-75b778cbb551?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1487530811176-3780de880c2d?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1535842802993-71e182ad4f23?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1519378058457-4c29c926d6e8?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1559561853-084c0b1b6b88?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1561181286-d3fee7d55364?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1596815064285-7ed0c2216b5a?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1589123053640-8fd29e4a2c4e?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1549480017-d76466a0b2d0?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1594671146480-dc26e3086c4f?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1563241527-3004b7be0ffd?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1596436889106-be35e843f974?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1578956520118-613e9e8d45f1?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1567696153792-87f41e9b8823?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1559561853-084c0b1b6b88?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1470509037661-1e2d5e5f8b5f?w=400&h=300&fit=crop"
            };
            int idx = (int) (ramo.getId() % flowerImages.length);
            foto = flowerImages[idx];
        }
        dto.setFoto(foto);
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

    public void seedCatalogo() {
        Optional<CategoriaRamo> catPredefinidos = categoriaRamoRepository
                .findByDescripcionCategoriaRamoIgnoreCase("Predefinidos");
        Optional<CategoriaRamo> catTemporada = categoriaRamoRepository
                .findByDescripcionCategoriaRamoIgnoreCase("Temporada");

        if (catPredefinidos.isEmpty() || catTemporada.isEmpty()) return;

        long countPredefinidos = ramoRepository.countByCategoriaRamo(catPredefinidos.get());
        long countTemporada = ramoRepository.countByCategoriaRamo(catTemporada.get());

        String[][] predefinidosData = {
            {"Ramo Clásico Rosado", "Ramo de rosas rosadas con eucalipto", "55.00"},
            {"Ramo Primaveral", "Girasoles y tulipanes de temporada", "48.00"},
            {"Lirios Elegance", "Lirios blancos minimalistas", "62.00"},
            {"Ramo Silvestre", "Flores silvestres variadas", "42.00"},
            {"Ramo de Lavanda", "Lavanda fresca con detalles blancos", "50.00"},
            {"Ramo Aromático", "Eucalipto y flores secas", "45.00"},
            {"Ramo de Rosas Rojas", "Doce rosas rojas eternas", "70.00"},
            {"Ramo Campestre", "Margaritas y flores de campo", "38.00"},
            {"Ramo Nupcial", "Peonías y hiedra blanca", "85.00"},
            {"Ramo Boho", "Flores silvestres en tonos pastel", "65.00"}
        };

        String[][] temporadaData = {
            {"Ramo San Valentín", "Rosas rojas en forma de corazón", "75.00"},
            {"Ramo de Otoño", "Girasoles y flores anaranjadas", "52.00"},
            {"Edición Primavera", "Tulipanes de colores variados", "58.00"},
            {"Ramo de Invierno", "Flores blancas y ramas de pino", "60.00"},
            {"Edición Verano", "Flores tropicales y hojas verdes", "55.00"},
            {"Ramo del Mes", "Selección especial del florista", "68.00"},
            {"Ramo Exótico", "Orquídeas y anturios", "80.00"},
            {"Ramo Fiesta", "Globos florales multicolor", "45.00"},
            {"Colección Vintage", "Rosas vintage y lavanda", "72.00"},
            {"Edición Premium", "Ramo gourmet con chocolate", "95.00"}
        };

        List<Ramo> nuevos = new ArrayList<>();

        for (int i = (int) countPredefinidos; i < 10 && i < predefinidosData.length; i++) {
            Ramo r = new Ramo();
            r.setNombreRamo(predefinidosData[i][0]);
            r.setDescripcionCorta(predefinidosData[i][1]);
            r.setDescripcionRamo(predefinidosData[i][1]);
            r.setPrecioRamo(new java.math.BigDecimal(predefinidosData[i][2]));
            r.setDisponible(true);
            r.setCategoriaRamo(catPredefinidos.get());
            r.setFechaCreacion(java.time.LocalDateTime.now().minusDays((long) (Math.random() * 30)));
            r.setDetallesRamo(new ArrayList<>());
            r.setFotoRamo("");
            nuevos.add(r);
        }

        for (int i = (int) countTemporada; i < 10 && i < temporadaData.length; i++) {
            Ramo r = new Ramo();
            r.setNombreRamo(temporadaData[i][0]);
            r.setDescripcionCorta(temporadaData[i][1]);
            r.setDescripcionRamo(temporadaData[i][1]);
            r.setPrecioRamo(new java.math.BigDecimal(temporadaData[i][2]));
            r.setDisponible(true);
            r.setCategoriaRamo(catTemporada.get());
            r.setFechaCreacion(java.time.LocalDateTime.now().minusDays((long) (Math.random() * 20)));
            r.setDetallesRamo(new ArrayList<>());
            r.setFotoRamo("");
            nuevos.add(r);
        }

        if (!nuevos.isEmpty()) {
            ramoRepository.saveAll(nuevos);
        }
    }

    private RamoResponseDTO toResponseDTO(Ramo ramo) {
        RamoResponseDTO dto = new RamoResponseDTO();
        dto.setId(ramo.getId());
        dto.setNombreRamo(ramo.getNombreRamo());
        dto.setPrecioRamo(ramo.getPrecioRamo());
        dto.setDescripcionRamo(ramo.getDescripcionRamo());

        String foto = ramo.getFotoRamo();
        if (foto != null && foto.contains("cloudinary.com/demo")) {
            String[] flowerImages = {
                "https://images.unsplash.com/photo-1566378246598-5b11a0d486cc?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1490750967868-88aa4f44baee?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1526047932273-341f2a7631f9?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1582794543139-8ac9cb0f7b11?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1468327768560-75b778cbb551?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1487530811176-3780de880c2d?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1535842802993-71e182ad4f23?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1519378058457-4c29c926d6e8?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1559561853-084c0b1b6b88?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1561181286-d3fee7d55364?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1596815064285-7ed0c2216b5a?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1589123053640-8fd29e4a2c4e?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1549480017-d76466a0b2d0?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1594671146480-dc26e3086c4f?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1563241527-3004b7be0ffd?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1596436889106-be35e843f974?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1578956520118-613e9e8d45f1?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1567696153792-87f41e9b8823?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1559561853-084c0b1b6b88?w=400&h=300&fit=crop",
                "https://images.unsplash.com/photo-1470509037661-1e2d5e5f8b5f?w=400&h=300&fit=crop"
            };
            int idx = (int) (ramo.getId() % flowerImages.length);
            foto = flowerImages[idx];
        }
        dto.setFotoRamo(foto);

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
