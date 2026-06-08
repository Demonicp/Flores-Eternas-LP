package flores.eternas.backend.exception;

/**
 * @author esteban
 * Excepcion personalizada para credenciales invalidas de login.
 */
public class CredencialesInvalidasException extends RuntimeException {

    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }
}