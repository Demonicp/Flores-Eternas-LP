package flores.eternas.backend.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author esteban
 * Entidad JPA que persiste los codigos de recuperacion de contrasena
 * solicitados por la administradora. Cada registro representa un codigo
 * de un solo uso con expiracion corta, asociado a un correo electronico.
 * El codigo se almacena hasheado con BCrypt; el codigo plano solo viaja
 * por el canal de email y nunca queda persistido en texto plano.
 */
@Entity
@Table(
    name = "password_reset_token",
    indexes = {
        @Index(name = "idx_password_reset_correo", columnList = "correo")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@AttributeOverride(name = "id", column = @Column(name = "id_password_reset_token"))
public class PasswordResetToken extends AbstractEntity {

    /**
     * @author esteban
     * Correo electronico al que se envio el codigo de recuperacion.
     * Se utiliza para localizar el token activo del usuario al momento
     * de validar el codigo y de aplicar el cambio de contrasena.
     */
    @Column(name = "correo", nullable = false, length = 150)
    private String correo;

    /**
     * @author esteban
     * Hash BCrypt del codigo de 6 digitos generado para este token.
     * Nunca se persiste el codigo en texto plano: al validar se compara
     * con BCrypt.matches(codigoIngresado, codigoHash).
     */
    @Column(name = "codigo_hash", nullable = false, length = 100)
    private String codigoHash;

    /**
     * @author esteban
     * Fecha y hora de expiracion del codigo. Una vez superada, el token
     * se considera invalido y debe solicitarse uno nuevo.
     */
    @Column(name = "expira_en", nullable = false)
    private LocalDateTime expiraEn;

    /**
     * @author esteban
     * Indica si el codigo ya fue consumido. Al cambiar la contrasena
     * exitosamente se marca como true para impedir reutilizacion.
     */
    @Column(name = "usado", nullable = false)
    private Boolean usado = false;

    /**
     * @author esteban
     * Contador de intentos fallidos de validacion del codigo.
     * Al alcanzar el maximo permitido, el token se invalida y se
     * obliga a solicitar un codigo nuevo.
     */
    @Column(name = "intentos", nullable = false)
    private Integer intentos = 0;

    /**
     * @author esteban
     * Fecha y hora de creacion del token. Se utiliza para ordenar
     * y obtener el token activo mas reciente por correo.
     */
    @Column(name = "creado_en", nullable = false)
    private LocalDateTime creadoEn;
}
