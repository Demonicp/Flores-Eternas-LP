package flores.eternas.backend.exception;

/**
 * @author esteban
 * Excepcion para errores de validacion de datos de entrada.
 * Se lanza cuando los datos no cumplen las reglas de negocio.
 */
public class ValidacionException extends RuntimeException {

    public ValidacionException(String mensaje) {
        super(mensaje);
    }
}
