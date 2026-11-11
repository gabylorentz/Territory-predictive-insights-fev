package dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import excecao.DadosInvalidosException;
import java.util.List;
import org.junit.jupiter.api.Test;

class PrevisaoTest {

    @Test
    void mediaMovelDeveUsarSomenteAsUltimasLeituras() throws DadosInvalidosException {
        // Arrange: 7 leituras, janela de 5 -> (20 + 25 + 30 + 28 + 35) / 5 = 27.6
        List<Medicao> historico = MedicoesDeTeste.de("Chuva", "mm/h", 10, 12, 20, 25, 30, 28, 35);
        PrevisaoMediaMovel estrategia = new PrevisaoMediaMovel(5);

        // Act
        double previsto = estrategia.prever(historico);

        // Assert
        assertEquals(27.6, previsto, 0.01);
    }

    @Test
    void mediaMovelDeveLancarExcecaoComHistoricoInsuficiente() throws DadosInvalidosException {
        // Arrange: só 3 leituras, e a janela pede 5
        List<Medicao> historico = MedicoesDeTeste.de("Chuva", "mm/h", 10, 20, 30);
        PrevisaoMediaMovel estrategia = new PrevisaoMediaMovel(5);

        // Act + Assert
        assertThrows(DadosInvalidosException.class, () -> estrategia.prever(historico));
    }

    @Test
    void comparacaoDeveProjetarTendenciaDeAlta() throws DadosInvalidosException {
        // Arrange: anterior = 12, recente = 22, tendência = +10, previsão = 22 + 10 = 32
        List<Medicao> historico = MedicoesDeTeste.de("Chuva", "mm/h", 10, 12, 14, 20, 22, 24);
        PrevisaoComparacao estrategia = new PrevisaoComparacao(3);

        // Act
        double previsto = estrategia.prever(historico);

        // Assert
        assertEquals(32.0, previsto, 0.01);
    }

    @Test
    void comparacaoDeveProjetarTendenciaDeQueda() throws DadosInvalidosException {
        // Arrange: anterior = 30, recente = 20, tendência = -10, previsão = 20 - 10 = 10
        List<Medicao> historico = MedicoesDeTeste.de("Chuva", "mm/h", 30, 30, 30, 20, 20, 20);
        PrevisaoComparacao estrategia = new PrevisaoComparacao(3);

        // Act
        double previsto = estrategia.prever(historico);

        // Assert
        assertEquals(10.0, previsto, 0.01);
    }

    @Test
    void comparacaoNaoDevePreverValorNegativo() throws DadosInvalidosException {
        // Arrange: anterior = 30, recente = 5, tendência = -25, conta = -20, e o resultado vira 0
        List<Medicao> historico = MedicoesDeTeste.de("Chuva", "mm/h", 30, 30, 30, 5, 5, 5);
        PrevisaoComparacao estrategia = new PrevisaoComparacao(3);

        // Act
        double previsto = estrategia.prever(historico);

        // Assert
        assertEquals(0.0, previsto, 0.01);
    }

    @Test
    void comparacaoDeveLancarExcecaoComHistoricoInsuficiente() throws DadosInvalidosException {
        // Arrange: 5 leituras, e a comparação com período 3 precisa de 6
        List<Medicao> historico = MedicoesDeTeste.de("Chuva", "mm/h", 10, 20, 30, 40, 50);
        PrevisaoComparacao estrategia = new PrevisaoComparacao(3);

        // Act + Assert
        assertThrows(DadosInvalidosException.class, () -> estrategia.prever(historico));
    }
}