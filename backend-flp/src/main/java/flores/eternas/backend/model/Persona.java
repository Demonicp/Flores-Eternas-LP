package flores.eternas.backend.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "persona")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@AttributeOverride(name = "id", column = @Column(name = "id_persona"))
public class Persona extends AbstractEntity {

    @Column(name = "nombre_cliente")
    private String nombreCliente;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    private String telefono;

    private String cedula;
}
