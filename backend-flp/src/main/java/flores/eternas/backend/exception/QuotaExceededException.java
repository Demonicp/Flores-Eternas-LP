package flores.eternas.backend.exception;

/**
 * @author esteban
 * Excepcion lanzada cuando la API de Gemini supera la cuota gratuita (HTTP 429 / RESOURCE_EXHAUSTED).
 * Activa el fallback a Hugging Face.
 */
public class QuotaExceededException extends RuntimeException {

    public QuotaExceededException(String mensaje) {
        super(mensaje);
    }
}