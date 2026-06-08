package flores.eternas.backend.exception;

/**
 * @author esteban
 * Excepcion personalizada para cuando el registro esta cerrado
 * (ya existe un administrador registrado).
 */
public class RegistroCerradoException extends RuntimeException {

    public RegistroCerradoException(String mensaje) {
        super(mensaje);
    }
}