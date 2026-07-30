package flores.eternas.backend.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import flores.eternas.backend.model.PasswordResetToken;

/**
 * @author esteban
 * Tests de integracion JPA para {@link PasswordResetTokenRepository}.
 * Usa H2 en memoria via el perfil "test".
 */
@DataJpaTest
@ActiveProfiles("test")
class PasswordResetTokenRepositoryTest {

    @Autowired
    private PasswordResetTokenRepository repository;

    private static final String CORREO = "admin@floreseternas.com";

    @BeforeEach
    void limpiar() {
        repository.deleteAll();
    }

    private PasswordResetToken nuevoToken(String correo) {
        PasswordResetToken t = new PasswordResetToken();
        t.setCorreo(correo);
        t.setCodigoHash("$2a$10$hashGenericoParaTests");
        t.setExpiraEn(LocalDateTime.now().plusMinutes(10));
        t.setUsado(false);
        t.setIntentos(0);
        t.setCreadoEn(LocalDateTime.now());
        return t;
    }

    @Test
    void testGuardarYBuscarPorId() {
        PasswordResetToken guardado = repository.save(nuevoToken(CORREO));

        Optional<PasswordResetToken> encontrado = repository.findById(guardado.getId());
        assertTrue(encontrado.isPresent());
        assertEquals(CORREO, encontrado.get().getCorreo());
        assertFalse(encontrado.get().getUsado());
        assertEquals(0, encontrado.get().getIntentos());
    }

    @Test
    void testFindFirstByCorreoAndUsadoFalse_DevuelveUnicoTokenActivo() {
        repository.save(nuevoToken(CORREO));

        Optional<PasswordResetToken> resultado =
                repository.findFirstByCorreoAndUsadoFalseOrderByCreadoEnDesc(CORREO);

        assertTrue(resultado.isPresent());
        assertEquals(CORREO, resultado.get().getCorreo());
    }

    @Test
    void testFindFirstByCorreoAndUsadoFalse_ExcluyeTokensUsados() {
        PasswordResetToken usado = nuevoToken(CORREO);
        usado.setUsado(true);
        repository.save(usado);

        Optional<PasswordResetToken> resultado =
                repository.findFirstByCorreoAndUsadoFalseOrderByCreadoEnDesc(CORREO);

        assertTrue(resultado.isEmpty(),
                "No debe retornar tokens marcados como usados");
    }

    @Test
    void testFindFirstByCorreoAndUsadoFalse_OrdenaPorMasReciente() {
        PasswordResetToken viejo = nuevoToken(CORREO);
        viejo.setCreadoEn(LocalDateTime.now().minusMinutes(5));
        repository.save(viejo);

        PasswordResetToken nuevo = nuevoToken(CORREO);
        nuevo.setCreadoEn(LocalDateTime.now());
        repository.save(nuevo);

        Optional<PasswordResetToken> resultado =
                repository.findFirstByCorreoAndUsadoFalseOrderByCreadoEnDesc(CORREO);

        assertTrue(resultado.isPresent());
        assertEquals(nuevo.getId(), resultado.get().getId(),
                "Debe retornar el token mas reciente");
    }

    @Test
    void testFindFirstByCorreoAndUsadoFalse_SinTokens_DevuelveVacio() {
        Optional<PasswordResetToken> resultado =
                repository.findFirstByCorreoAndUsadoFalseOrderByCreadoEnDesc("noexiste@test.com");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindFirstByCorreoAndUsadoFalse_SoloRetornaUnoPorCorreo() {
        repository.save(nuevoToken(CORREO));
        repository.save(nuevoToken(CORREO));
        repository.save(nuevoToken(CORREO));

        Optional<PasswordResetToken> resultado =
                repository.findFirstByCorreoAndUsadoFalseOrderByCreadoEnDesc(CORREO);

        assertTrue(resultado.isPresent());
    }

    @Test
    void testDeleteByCorreo_EliminaTodosLosTokens() {
        repository.save(nuevoToken(CORREO));
        repository.save(nuevoToken(CORREO));
        repository.save(nuevoToken("otro@test.com"));

        repository.deleteByCorreo(CORREO);

        List<PasswordResetToken> restantes = repository.findAll();
        assertEquals(1, restantes.size());
        assertEquals("otro@test.com", restantes.get(0).getCorreo());
    }

    @Test
    void testActualizarUsadoTrue_PersisteCambio() {
        PasswordResetToken token = repository.save(nuevoToken(CORREO));

        token.setUsado(true);
        repository.save(token);

        PasswordResetToken recargado = repository.findById(token.getId()).orElse(null);
        assertNotNull(recargado);
        assertTrue(recargado.getUsado());
    }
}
