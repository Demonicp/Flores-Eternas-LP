package flores.eternas.backend.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * @author esteban
 * Tests unitarios del helper {@link EmailTemplates}.
 * Verifica que el HTML del correo de recuperacion tenga la estructura
 * esperada y contenga el codigo generado en la caja destacada.
 */
class EmailTemplatesTest {

    @Test
    void testCodigoRecuperacion_RetornaHtmlNoNulo() {
        String html = EmailTemplates.codigoRecuperacion("482917");
        assertNotNull(html);
        assertTrue(html.length() > 0);
    }

    @Test
    void testCodigoRecuperacion_ContieneElCodigoPlano() {
        String codigo = "482917";
        String html = EmailTemplates.codigoRecuperacion(codigo);
        assertTrue(html.contains(codigo),
                "El HTML debe contener el codigo en texto plano");
    }

    @Test
    void testCodigoRecuperacion_ContieneBranding() {
        String html = EmailTemplates.codigoRecuperacion("482917");
        assertTrue(html.contains("Flores Eternas LP"),
                "El HTML debe incluir el nombre comercial");
    }

    @Test
    void testCodigoRecuperacion_MencionaExpiracion10Minutos() {
        String html = EmailTemplates.codigoRecuperacion("482917");
        assertTrue(html.contains("10 minutos"),
                "El HTML debe mencionar la ventana de expiracion de 10 minutos");
    }

    @Test
    void testCodigoRecuperacion_ContieneDisclaimerSeguridad() {
        String html = EmailTemplates.codigoRecuperacion("482917");
        assertTrue(html.contains("¿No solicitaste este cambio?"),
                "El HTML debe incluir el aviso de seguridad");
    }

    @Test
    void testCodigoRecuperacion_EmpiezaConDoctype() {
        String html = EmailTemplates.codigoRecuperacion("482917");
        assertTrue(html.startsWith("<!DOCTYPE html>"),
                "El HTML debe empezar con declaracion de DOCTYPE");
    }

    @Test
    void testCodigoRecuperacion_TituloCorrecto() {
        String html = EmailTemplates.codigoRecuperacion("482917");
        assertTrue(html.contains(EmailTemplates.ASUNTO_RECUPERACION),
                "El titulo debe coincidir con el asunto del correo");
    }

    @Test
    void testAsuntoConstante_TieneElValorEsperado() {
        assertEquals("Recuperación de contraseña - Flores Eternas LP",
                EmailTemplates.ASUNTO_RECUPERACION);
    }

    @Test
    void testCodigoRecuperacion_ContieneBloqueEstiladoDelCodigo() {
        String html = EmailTemplates.codigoRecuperacion("482917");
        assertTrue(html.contains("font-size:36px"),
                "El HTML debe contener el bloque estilado para el codigo");
        assertTrue(html.contains("Courier New"),
                "El HTML debe usar fuente monoespaciada para el codigo");
    }

    @Test
    void testCodigoRecuperacion_UsaTuteoEnElDisclaimer() {
        String html = EmailTemplates.codigoRecuperacion("482917");
        assertTrue(html.contains("tú") || html.contains("Tú"),
                "El disclaimer debe estar escrito en tuteo (tu/tú), no en voseo");
        assertTrue(html.contains("puedes"),
                "El disclaimer debe usar la forma 'puedes' del verbo poder");
    }
}
