package flores.eternas.backend.services;

/**
 * @author esteban
 * Helper con plantillas HTML para los correos transaccionales del sistema
 * de recuperacion de contrasena. Centraliza el HTML en un solo lugar para
 * que cualquier ajuste de diseno o de copy se haga en un unico archivo.
 *
 * Nota de implementacion: el HTML usa estilos inline (no hojas de estilo
 * externas ni bloques {@code <style>}) porque muchos clientes de correo
 * (Gmail, Outlook) descartan o limitan el CSS embebido. Las tablas se
 * usan para el layout porque siguen siendo la forma mas confiable de
 * lograr disenos responsivos en clientes de email.
 */
public final class EmailTemplates {

    private EmailTemplates() {
    }

    /**
     * @author esteban
     * Asunto del correo de recuperacion de contrasena.
     */
    public static final String ASUNTO_RECUPERACION = "Recuperación de contraseña - Flores Eternas LP";

    /**
     * @author esteban
     * Construye el cuerpo HTML del correo que contiene el codigo de
     * recuperacion de contrasena. El codigo se inyecta en una caja
     * centrada, monoespaciada y de gran tamano para que sea facil de
     * leer y transcribir.
     * @param codigoPlano Codigo de 6 digitos que recibira el usuario.
     *                     El servicio es responsable de generarlo con
     *                     SecureRandom y nunca debe llegar aca hasheado.
     * @return String con el HTML completo listo para enviar por SMTP.
     */
    public static String codigoRecuperacion(String codigoPlano) {
        String estilos = ""
                + "margin:0; padding:0; background-color:#FCF9F6; "
                + "font-family:'Helvetica Neue', Arial, sans-serif; color:#5E3A1F;";
        String estilosTabla = ""
                + "max-width:560px; background-color:#FFFFFF; border-radius:16px; "
                + "border:1px solid #E8D5C8;";
        String estilosHeader = ""
                + "padding:32px 32px 16px 32px; text-align:center; "
                + "border-bottom:1px solid #FCEEE3;";
        String estilosTitulo = ""
                + "margin:0; font-family:Georgia, serif; font-size:24px; "
                + "color:#8C5A3C; font-weight:normal;";
        String estilosCuerpo = "padding:32px;";
        String estilosH2 = ""
                + "margin:0 0 16px 0; font-family:Georgia, serif; font-size:20px; "
                + "color:#7A4E2D; font-weight:normal;";
        String estilosParrafo = ""
                + "margin:0 0 16px 0; font-size:15px; line-height:1.6; color:#5E3A1F;";
        String estilosCajaCodigo = ""
                + "background-color:#FFEDE3; border:2px dashed #F0D3C1; border-radius:12px; "
                + "padding:24px; text-align:center; margin:24px 0;";
        String estilosEtiquetaCodigo = ""
                + "margin:0 0 8px 0; font-size:12px; text-transform:uppercase; "
                + "letter-spacing:2px; color:#8C5A3C;";
        String estilosCodigo = ""
                + "margin:0; font-family:'Courier New', monospace; font-size:36px; "
                + "font-weight:bold; letter-spacing:8px; color:#7A4E2D;";
        String estilosAviso = ""
                + "background-color:#FCEEE3; border-left:4px solid #F0D3C1; "
                + "padding:12px 16px; border-radius:4px; margin:24px 0;";
        String estilosAvisoTexto = ""
                + "margin:0; font-size:13px; line-height:1.5; color:#5E3A1F;";
        String estilosFooter = ""
                + "padding:16px 32px 32px 32px; text-align:center; "
                + "border-top:1px solid #FCEEE3;";
        String estilosFooterTexto = "margin:0; font-size:12px; color:#8C5A3C;";

        return ""
                + "<!DOCTYPE html>"
                + "<html lang=\"es\">"
                + "<head><meta charset=\"UTF-8\">"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                + "<title>" + ASUNTO_RECUPERACION + "</title>"
                + "</head>"
                + "<body style=\"" + estilos + "\">"
                +   "<table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" "
                +          "width=\"100%\" style=\"" + estilos + "padding:32px 16px;\">"
                +     "<tr><td align=\"center\">"
                +       "<table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" "
                +              "width=\"100%\" style=\"" + estilosTabla + "\">"
                +         "<tr><td style=\"" + estilosHeader + "\">"
                +           "<h1 style=\"" + estilosTitulo + "\">Flores Eternas LP</h1>"
                +         "</td></tr>"
                +         "<tr><td style=\"" + estilosCuerpo + "\">"
                +           "<h2 style=\"" + estilosH2 + "\">Recuperación de contraseña</h2>"
                +           "<p style=\"" + estilosParrafo + "\">Hola,</p>"
                +           "<p style=\"" + estilosParrafo + "\">"
                +             "Recibimos una solicitud para restablecer la contraseña de tu cuenta "
                +             "de administración. Usa el siguiente código para continuar con el proceso:"
                +           "</p>"
                +           "<div style=\"" + estilosCajaCodigo + "\">"
                +             "<p style=\"" + estilosEtiquetaCodigo + "\">Tu código</p>"
                +             "<p style=\"" + estilosCodigo + "\">" + codigoPlano + "</p>"
                +           "</div>"
                +           "<p style=\"" + estilosParrafo + "\">"
                +             "Este código expirará en <strong>10 minutos</strong>."
                +           "</p>"
                +           "<div style=\"" + estilosAviso + "\">"
                +             "<p style=\"" + estilosAvisoTexto + "\">"
                +               "<strong>¿No solicitaste este cambio?</strong><br>"
                +               "Si no fuiste tú quien pidió recuperar la contraseña, puedes ignorar "
                +               "este mensaje. Tu contraseña actual seguirá siendo válida."
                +             "</p>"
                +           "</div>"
                +         "</td></tr>"
                +         "<tr><td style=\"" + estilosFooter + "\">"
                +           "<p style=\"" + estilosFooterTexto + "\">"
                +             "Flores Eternas LP &mdash; Palmira, Valle del Cauca"
                +           "</p>"
                +         "</td></tr>"
                +       "</table>"
                +     "</td></tr>"
                +   "</table>"
                + "</body>"
                + "</html>";
    }
}
