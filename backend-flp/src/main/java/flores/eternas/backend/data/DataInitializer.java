package flores.eternas.backend.data;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import flores.eternas.backend.model.CategoriaObjeto;
import flores.eternas.backend.model.CategoriaRamo;
import flores.eternas.backend.model.ColorFlor;
import flores.eternas.backend.model.Inventario;
import flores.eternas.backend.model.TipoFlor;
import flores.eternas.backend.model.enums.Estado;
import flores.eternas.backend.repository.CategoriaObjetoRepository;
import flores.eternas.backend.repository.CategoriaRamoRepository;
import flores.eternas.backend.repository.ColorFlorRepository;
import flores.eternas.backend.repository.InventarioRepository;
import flores.eternas.backend.repository.TipoFlorRepository;

@Component
@Order(2)
public class DataInitializer implements CommandLineRunner {

    private final TipoFlorRepository tipoFlorRepository;
    private final ColorFlorRepository colorFlorRepository;
    private final InventarioRepository inventarioRepository;
    private final CategoriaRamoRepository categoriaRamoRepository;
    private final CategoriaObjetoRepository categoriaObjetoRepository;

    public DataInitializer(
            TipoFlorRepository tipoFlorRepository,
            ColorFlorRepository colorFlorRepository,
            InventarioRepository inventarioRepository,
            CategoriaRamoRepository categoriaRamoRepository,
            CategoriaObjetoRepository categoriaObjetoRepository) {
        this.tipoFlorRepository = tipoFlorRepository;
        this.colorFlorRepository = colorFlorRepository;
        this.inventarioRepository = inventarioRepository;
        this.categoriaRamoRepository = categoriaRamoRepository;
        this.categoriaObjetoRepository = categoriaObjetoRepository;
    }

    @Override
    public void run(String... args) {
        if (inventarioRepository.count() > 0) {
            return;
        }

        if (categoriaRamoRepository.findByDescripcionCategoriaRamoIgnoreCase("Personalizado").isEmpty()) {
            CategoriaRamo catPersonalizado = new CategoriaRamo();
            catPersonalizado.setDescripcionCategoriaRamo("Personalizado");
            categoriaRamoRepository.save(catPersonalizado);
        }

        if (tipoFlorRepository.findAll().stream().noneMatch(t -> t.getDescripcionFlor().equalsIgnoreCase("Clavel"))) {
            TipoFlor clavel = new TipoFlor();
            clavel.setDescripcionFlor("Clavel");
            clavel.setPrecioUnidad(new BigDecimal("1.50"));
            clavel.setPorcentajePorMayor(new BigDecimal("0.10"));
            tipoFlorRepository.save(clavel);
        }

        for (String nombreColor : List.of("Rosa", "Morado", "Naranja")) {
            if (colorFlorRepository.findAll().stream().noneMatch(c -> c.getDescripcionColor().equalsIgnoreCase(nombreColor))) {
                ColorFlor color = new ColorFlor();
                color.setDescripcionColor(nombreColor);
                colorFlorRepository.save(color);
            }
        }

        CategoriaObjeto catAccesorio;
        if (categoriaObjetoRepository.findAll().stream().noneMatch(c -> c.getDescripcionCategoria().equalsIgnoreCase("Accesorio"))) {
            catAccesorio = new CategoriaObjeto();
            catAccesorio.setDescripcionCategoria("Accesorio");
            catAccesorio = categoriaObjetoRepository.save(catAccesorio);
        } else {
            catAccesorio = categoriaObjetoRepository.findAll().stream()
                    .filter(c -> c.getDescripcionCategoria().equalsIgnoreCase("Accesorio"))
                    .findFirst().orElseThrow();
        }

        List<Inventario> adiciones = List.of(
                crearInventario("Corona", "Corona decorativa para ramo", 20, new BigDecimal("5.00"), Estado.DISPONIBLE,
                        catAccesorio),
                crearInventario("Moño", "Moño de tela para ramo", 50, new BigDecimal("1.50"), Estado.DISPONIBLE,
                        catAccesorio),
                crearInventario("Tarjeta", "Tarjeta personalizada", 100, new BigDecimal("2.00"), Estado.DISPONIBLE,
                        catAccesorio),
                crearInventario("Papel de regalo", "Papel decorativo para envolver", 30, new BigDecimal("3.00"),
                        Estado.DISPONIBLE, catAccesorio));
        inventarioRepository.saveAll(adiciones);
    }

    private TipoFlor crearTipoFlor(String descripcion, BigDecimal precio, BigDecimal porcentaje) {
        TipoFlor flor = new TipoFlor();
        flor.setDescripcionFlor(descripcion);
        flor.setPrecioUnidad(precio);
        flor.setPorcentajePorMayor(porcentaje);
        return flor;
    }

    private ColorFlor crearColor(String descripcion) {
        ColorFlor color = new ColorFlor();
        color.setDescripcionColor(descripcion);
        return color;
    }

    private Inventario crearInventario(String nombre, String descripcion, int stock, BigDecimal precio, Estado estado,
            CategoriaObjeto categoria) {
        Inventario item = new Inventario();
        item.setNombreInventario(nombre);
        item.setDescripcionInventario(descripcion);
        item.setStock(stock);
        item.setPrecioCosto(precio);
        item.setEstado(estado);
        item.setCategoria(categoria);
        return item;
    }
}
