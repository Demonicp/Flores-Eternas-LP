package flores.eternas.backend.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import flores.eternas.backend.model.Local;
import flores.eternas.backend.services.LocalService;

/**
 * Tests de integracion del controller de {@link LocalController}.
 * Usa MockMvc para enviar requests HTTP reales y verificar el endpoint
 * publico de locales activos para el desplegable de retiro.
 * El endpoint de administracion no se cubre por seguridad (no se generan
 * tokens de admin en tests).
 *
 * @author santiago (sesion 05/08/2026 - modulo de retiro en local)
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LocalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LocalService localService;

    /* ─── GET /api/locales ─── */

    @Test
    void testListarActivos_Devuelve200ConLista() throws Exception {
        Local local = new Local();
        local.setId(1L);
        local.setNombreLocal("Local Principal");
        local.setDireccion("Calle 25 #28-32");
        local.setCiudad("Palmira");
        local.setRegion("Valle del Cauca");
        local.setActivo(true);

        when(localService.listarActivos()).thenReturn(List.of(local));

        mockMvc.perform(get("/api/locales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombreLocal").value("Local Principal"))
                .andExpect(jsonPath("$[0].ciudad").value("Palmira"))
                .andExpect(jsonPath("$[0].region").value("Valle del Cauca"));

        verify(localService).listarActivos();
    }

    @Test
    void testListarActivos_SinLocales_Devuelve200ConListaVacia() throws Exception {
        when(localService.listarActivos()).thenReturn(List.of());

        mockMvc.perform(get("/api/locales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(localService).listarActivos();
    }
}