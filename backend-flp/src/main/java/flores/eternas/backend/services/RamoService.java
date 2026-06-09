package flores.eternas.backend.services;

import org.springframework.stereotype.Service;

import flores.eternas.backend.model.Ramo;
import flores.eternas.backend.repository.RamoRepository;

@Service
public class RamoService {
    private  RamoRepository ramoRepository;


    public Ramo crearRamo(Ramo ramo) {
        return ramoRepository.save(ramo);
        
     
    }
    
}
