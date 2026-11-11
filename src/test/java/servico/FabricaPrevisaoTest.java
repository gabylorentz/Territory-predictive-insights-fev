package servico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dominio.EstrategiaPrevisao;
import dominio.PrevisaoComparacao;
import dominio.PrevisaoMediaMovel;
import excecao.DadosInvalidosException;
import java.util.List;
import org.junit.jupiter.api.Test;

class FabricaPrevisaoTest {

    @Test
    void deveCriarMediaMovelPeloNome() throws DadosInvalidosException {
        // Arrange
        FabricaPrevisao fabrica = new FabricaPrevisao();
        // Act
        EstrategiaPrevisao estrategia = fabrica.criar("media");
        // Assert
        assertEquals(PrevisaoMediaMovel.class, estrategia.getClass());
    }

    @Test
    void deveCriarComparacaoPeloNome() throws DadosInvalidosException {
        // Arrange
        FabricaPrevisao fabrica = new FabricaPrevisao();
        // Act
        EstrategiaPrevisao estrategia = fabrica.criar("comparacao");
        // Assert
        assertEquals(PrevisaoComparacao.class, estrategia.getClass());
    }

    @Test
    void deveLancarExcecaoParaTipoDesconhecido() {
        // Arrange
        FabricaPrevisao fabrica = new FabricaPrevisao();
        // Act + Assert
        assertThrows(DadosInvalidosException.class, () -> fabrica.criar("tendencia"));
    }

    @Test
    void criarTodasDeveDevolverAsDuasEstrategias() {
        // Arrange
        FabricaPrevisao fabrica = new FabricaPrevisao();
        // Act
        List<EstrategiaPrevisao> todas = fabrica.criarTodas();
        // Assert
        assertEquals(2, todas.size());
    }

    @Test
    void configuracaoDeveSerSingleton() {
        // Arrange + Act
        ConfiguracaoSistema a = ConfiguracaoSistema.getInstancia();
        ConfiguracaoSistema b = ConfiguracaoSistema.getInstancia();
        // Assert
        assertSame(a, b);
    }
}