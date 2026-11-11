package dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import excecao.DadosInvalidosException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class MedicaoTest {

    @Test
    void deveCriarMedicaoQuandoDadosForemValidos() throws DadosInvalidosException {
        // Arrange
        LocalDateTime agora = LocalDateTime.now();

        // Act
        Medicao medicao = new Medicao(null, "Chuva", 42.5, "mm/h", agora);

        // Assert
        assertEquals("Chuva", medicao.getTipoSensor());
        assertEquals(42.5, medicao.getValor(), 0.001);
        assertEquals("mm/h", medicao.getUnidade());
    }

    @Test
    void deveLancarExcecaoQuandoValorForNegativo() {
        // Arrange
        LocalDateTime agora = LocalDateTime.now();

        // Act + Assert
        DadosInvalidosException erro = assertThrows(DadosInvalidosException.class,
                () -> new Medicao(null, "Chuva", -5, "mm/h", agora));
        assertTrue(erro.getMessage().contains("Chuva"));
    }

    @Test
    void deveLancarExcecaoQuandoValorForNaN() {
        // Arrange
        LocalDateTime agora = LocalDateTime.now();

        // Act + Assert
        assertThrows(DadosInvalidosException.class,
                () -> new Medicao(null, "Chuva", Double.NaN, "mm/h", agora));
    }

    @Test
    void deveLancarExcecaoQuandoTipoSensorForVazio() {
        // Arrange
        LocalDateTime agora = LocalDateTime.now();

        // Act + Assert
        assertThrows(DadosInvalidosException.class,
                () -> new Medicao(null, "", 10, "mm/h", agora));
    }
}