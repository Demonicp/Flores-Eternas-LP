package flores.eternas.backend.model;

import flores.eternas.backend.model.enums.Rol;
import jakarta.persistence.CascadeType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@AttributeOverride(name = "id", column = @Column(name = "id_usuario"))
public class Usuario extends AbstractEntity {

    @Column(name = "correo_electronico")
    private String correoElectronico;

    private String contrasena;

    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY, cascade = CascadeType.ALL) // @author esteban - cascade para guardar Persona junto con Usuario
    @JoinColumn(name = "id_persona")
    private Persona persona;

    @Enumerated(EnumType.STRING)
    private Rol rol;
}
