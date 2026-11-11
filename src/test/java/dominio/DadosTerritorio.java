package dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import excecao.DadosInvalidosException;
import org.junit.jupiter.api.Test;

class DadosTerritorioTest {

    @Test
    void deveAlterarLimiarValido() throws DadosInvalidosException {
        // Arrange
        DadosTerritorio dados = new DadosTerritorio();

        // Act
        dados.setLimiar("Chuva", 40.0);

        // Assert
        assertEquals(40.0, dados.getLimiar("Chuva"), 0.01);
    }

    @Test
    void deveRejeitarLimiarNegativoSemAlterarOValorAnterior() throws DadosInvalidosException {
        // Arrange
        DadosTerritorio dados = new DadosTerritorio();

        // Act + Assert 1: a exceção acontece
        assertThrows(DadosInvalidosException.class, () -> dados.setLimiar("Chuva", -10));

        // Assert 2: o valor padrão continua 30.0
        assertEquals(30.0, dados.getLimiar("Chuva"), 0.01);
    }

    @Test
    void deveRejeitarSensorSemLimiarCadastradoAoAlterar() {
        // Arrange
        DadosTerritorio dados = new DadosTerritorio();

        // Act + Assert
        assertThrows(DadosInvalidosException.class, () -> dados.setLimiar("Chuvaa", 40.0));
    }

    @Test
    void deveRejeitarConsultaDeLimiarDeSensorInexistente() {
        // Arrange
        DadosTerritorio dados = new DadosTerritorio();

        // Act + Assert
        assertThrows(DadosInvalidosException.class, () -> dados.getLimiar("Vento"));
    }
}