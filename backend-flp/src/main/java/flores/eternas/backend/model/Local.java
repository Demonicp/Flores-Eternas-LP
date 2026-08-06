package flores.eternas.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa un local (punto de retiro) registrado por la administradora.
 * El cliente puede elegir recoger su pedido en uno de estos locales; al
 * hacerlo, el pedido guarda internamente la direccion del local elegido.
 *
 * @author esteban
 * @author santiago (sesion 05/08/2026 - modulo de retiro en local)
 */
@Data
@Entity
@Table(name = "local")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Local extends AbstractEntity {

    @Column(name = "nombre_local")
    private String nombreLocal;

    @Column(name = "direccion")
    private String direccion;

    @Column(name = "ciudad")
    private String ciudad;

    @Column(name = "region")
    private String region;

    @Column(name = "activo")
    private boolean activo = true;
}
