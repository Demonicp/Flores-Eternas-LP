package flores.eternas.backend.config;

import flores.eternas.backend.model.*;
import flores.eternas.backend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final CategoriaRamoRepository categoriaRamoRepository;
    private final TipoFlorRepository tipoFlorRepository;
    private final ColorFlorRepository colorFlorRepository;
    private final RamoRepository ramoRepository;

    public DataSeeder(CategoriaRamoRepository categoriaRamoRepository,
                      TipoFlorRepository tipoFlorRepository,
                      ColorFlorRepository colorFlorRepository,
                      RamoRepository ramoRepository) {
        this.categoriaRamoRepository = categoriaRamoRepository;
        this.tipoFlorRepository = tipoFlorRepository;
        this.colorFlorRepository = colorFlorRepository;
        this.ramoRepository = ramoRepository;
    }

    @Override
    public void run(String... args) {
        if (categoriaRamoRepository.count() > 0) {
            return;
        }

        CategoriaRamo predefinidos = new CategoriaRamo();
        predefinidos.setDescripcionCategoriaRamo("Predefinidos");
        categoriaRamoRepository.save(predefinidos);

        CategoriaRamo temporada = new CategoriaRamo();
        temporada.setDescripcionCategoriaRamo("Temporada");
        categoriaRamoRepository.save(temporada);

        TipoFlor rosa = new TipoFlor();
        rosa.setDescripcionFlor("Rosa");
        rosa.setPrecioUnidad(new BigDecimal("3.50"));
        rosa.setPorcentajePorMayor(new BigDecimal("10"));
        tipoFlorRepository.save(rosa);

        TipoFlor girasol = new TipoFlor();
        girasol.setDescripcionFlor("Girasol");
        girasol.setPrecioUnidad(new BigDecimal("2.80"));
        girasol.setPorcentajePorMayor(new BigDecimal("10"));
        tipoFlorRepository.save(girasol);

        TipoFlor tulipan = new TipoFlor();
        tulipan.setDescripcionFlor("Tulipán");
        tulipan.setPrecioUnidad(new BigDecimal("4.00"));
        tulipan.setPorcentajePorMayor(new BigDecimal("15"));
        tipoFlorRepository.save(tulipan);

        TipoFlor lirio = new TipoFlor();
        lirio.setDescripcionFlor("Lirio");
        lirio.setPrecioUnidad(new BigDecimal("3.20"));
        lirio.setPorcentajePorMayor(new BigDecimal("10"));
        tipoFlorRepository.save(lirio);

        ColorFlor rojo = new ColorFlor();
        rojo.setDescripcionColor("Rojo");
        colorFlorRepository.save(rojo);

        ColorFlor blanco = new ColorFlor();
        blanco.setDescripcionColor("Blanco");
        colorFlorRepository.save(blanco);

        ColorFlor amarillo = new ColorFlor();
        amarillo.setDescripcionColor("Amarillo");
        colorFlorRepository.save(amarillo);

        ColorFlor rosado = new ColorFlor();
        rosado.setDescripcionColor("Rosado");
        colorFlorRepository.save(rosado);

        Ramo ramo1 = new Ramo();
        ramo1.setNombreRamo("Ramo Clásico Rosado");
        ramo1.setDescripcionCorta("Elegante ramo de rosas rosadas");
        ramo1.setDescripcionRamo("Un hermoso ramo compuesto por una docena de rosas rosadas cuidadosamente seleccionadas. Ideal para cumpleaños, aniversarios o simplemente para alegrar el día de alguien especial. Incluye envoltura decorativa y moño.");
        ramo1.setPrecioRamo(new BigDecimal("45.00"));
        ramo1.setFotoRamo("https://res.cloudinary.com/demo/image/upload/v1/ramos/ramo-clasico-rosado");
        ramo1.setDisponible(true);
        ramo1.setCategoriaRamo(predefinidos);

        DetalleRamo detalle1 = new DetalleRamo();
        detalle1.setCantidad(12);
        detalle1.setTipoFlor(rosa);
        detalle1.setColorFlor(rosado);
        detalle1.setRamo(ramo1);
        ramo1.setDetallesRamo(List.of(detalle1));

        ramoRepository.save(ramo1);

        Ramo ramo2 = new Ramo();
        ramo2.setNombreRamo("Ramo Primaveral");
        ramo2.setDescripcionCorta("Mezcla alegre de girasoles y tulipanes");
        ramo2.setDescripcionRamo("Un ramo vibrante que combina girasoles amarillos con tulipanes blancos, perfecto para celebrar la llegada de la primavera. Incluye follaje decorativo y cinta de yute.");
        ramo2.setPrecioRamo(new BigDecimal("38.00"));
        ramo2.setFotoRamo("https://res.cloudinary.com/demo/image/upload/v1/ramos/ramo-primaveral");
        ramo2.setDisponible(true);
        ramo2.setCategoriaRamo(predefinidos);

        DetalleRamo detalle2a = new DetalleRamo();
        detalle2a.setCantidad(5);
        detalle2a.setTipoFlor(girasol);
        detalle2a.setColorFlor(amarillo);
        detalle2a.setRamo(ramo2);

        DetalleRamo detalle2b = new DetalleRamo();
        detalle2b.setCantidad(5);
        detalle2b.setTipoFlor(tulipan);
        detalle2b.setColorFlor(blanco);
        detalle2b.setRamo(ramo2);

        ramo2.setDetallesRamo(List.of(detalle2a, detalle2b));

        ramoRepository.save(ramo2);

        Ramo ramo3 = new Ramo();
        ramo3.setNombreRamo("Ramo San Valentín");
        ramo3.setDescripcionCorta("Rosas rojas en un arreglo de corazón");
        ramo3.setDescripcionRamo("El ramo perfecto para el día del amor. Veinticuatro rosas rojas dispuestas en forma de corazón, con detalles de baby breath y lazo rojo satinado. Incluye tarjeta personalizable.");
        ramo3.setPrecioRamo(new BigDecimal("75.00"));
        ramo3.setFotoRamo("https://res.cloudinary.com/demo/image/upload/v1/ramos/ramo-san-valentin");
        ramo3.setDisponible(true);
        ramo3.setCategoriaRamo(temporada);

        DetalleRamo detalle3 = new DetalleRamo();
        detalle3.setCantidad(24);
        detalle3.setTipoFlor(rosa);
        detalle3.setColorFlor(rojo);
        detalle3.setRamo(ramo3);
        ramo3.setDetallesRamo(List.of(detalle3));

        ramoRepository.save(ramo3);

        Ramo ramo4 = new Ramo();
        ramo4.setNombreRamo("Lirios Elegance");
        ramo4.setDescripcionCorta("Ramo minimalista de lirios blancos");
        ramo4.setDescripcionRamo("Un ramo sofisticado de lirios blancos, ideal para eventos formales o como centro de mesa. Incluye jarrón de vidrio y base de espuma floral.");
        ramo4.setPrecioRamo(new BigDecimal("55.00"));
        ramo4.setFotoRamo("https://res.cloudinary.com/demo/image/upload/v1/ramos/lirios-elegance");
        ramo4.setDisponible(false);
        ramo4.setCategoriaRamo(predefinidos);

        DetalleRamo detalle4 = new DetalleRamo();
        detalle4.setCantidad(10);
        detalle4.setTipoFlor(lirio);
        detalle4.setColorFlor(blanco);
        detalle4.setRamo(ramo4);
        ramo4.setDetallesRamo(List.of(detalle4));

        ramoRepository.save(ramo4);
    }
}
