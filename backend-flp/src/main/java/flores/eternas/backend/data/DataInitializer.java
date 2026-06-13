package flores.eternas.backend.data;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
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
        if (tipoFlorRepository.count() > 0) {
            return;
        }

        CategoriaRamo catPersonalizado = new CategoriaRamo();
        catPersonalizado.setDescripcionCategoriaRamo("Personalizado");
        categoriaRamoRepository.save(catPersonalizado);

        List<TipoFlor> flores = List.of(
                crearTipoFlor("Rosa", new BigDecimal("2.50"), new BigDecimal("0.10")),
                crearTipoFlor("Girasol", new BigDecimal("3.00"), new BigDecimal("0.10")),
                crearTipoFlor("Tulipán", new BigDecimal("2.00"), new BigDecimal("0.10")),
                crearTipoFlor("Clavel", new BigDecimal("1.50"), new BigDecimal("0.10")));
        tipoFlorRepository.saveAll(flores);

        List<ColorFlor> colores = List.of(
                crearColor("Rojo"),
                crearColor("Blanco"),
                crearColor("Amarillo"),
                crearColor("Rosa"),
                crearColor("Morado"),
                crearColor("Naranja"));
        colorFlorRepository.saveAll(colores);

        CategoriaObjeto catAccesorio = new CategoriaObjeto();
        catAccesorio.setDescripcionCategoria("Accesorio");
        categoriaObjetoRepository.save(catAccesorio);

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
