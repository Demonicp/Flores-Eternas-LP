package flores.eternas.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import flores.eternas.backend.dto.RamoDTO;
import flores.eternas.backend.model.Ramo;
import flores.eternas.backend.services.RamoService;

@RestController
@RequestMapping("/ramo")
public class RamoController {

    @Autowired
    private RamoService ramoService;

    @PostMapping("/crear")
    public ResponseEntity<Ramo> crearRamo(@RequestBody RamoDTO ramo) {




        Ramo ramoPersonalizado = new Ramo();
        ramoPersonalizado.setNombreRamo(ramo.getNombre());
        ramoPersonalizado.setDescripcionRamo(ramo.getDescripcion());
        ramoPersonalizado.setFotoRamo(ramo.getFoto());
        ramoPersonalizado.setPrecioRamo(ramo.getPrecio());
        ramoPersonalizado.setCategoriaRamo(ramo.getCategoria());
        ramoPersonalizado.setDetallesRamo(ramo.getDetalles());

        Ramo nuevoRamo;
        try {
            nuevoRamo = ramoService.crearRamo(ramoPersonalizado);
        } catch (Exception e) {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
        
        return ResponseEntity.ok(nuevoRamo);
    }
}
