package servico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dominio.Alerta;
import dominio.DadosTerritorio;
import dominio.Medicao;
import dominio.MedicoesDeTeste;
import dominio.NivelAlerta;
import dominio.PrevisaoMediaMovel;
import excecao.DadosInvalidosException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import persistencia.AlertaDAO;
import persistencia.MedicaoDAO;

@ExtendWith(MockitoExtension.class)
class ServicoMonitoramentoTest {

    @Mock
    private MedicaoDAO medicaoDAO;

    @Mock
    private AlertaDAO alertaDAO;

    private ServicoMonitoramento servico;

    @BeforeEach
    void montarServico() {
        servico = new ServicoMonitoramento(medicaoDAO, alertaDAO, new DadosTerritorio());
    }

    @Test
    void deveEmitirAlertaEGravarQuandoPrevisaoUltrapassarOLimiar() throws Exception {
        // Arrange: média 40 contra limiar 30 = 133% -> ALERTA
        List<Medicao> historico = MedicoesDeTeste.de("Chuva", "mm/h", 40, 40, 40, 40, 40);
        when(medicaoDAO.listarPorSensor("Chuva")).thenReturn(historico);

        // Act
        Optional<Alerta> resultado = servico.avaliar("Chuva", "mm/h", new PrevisaoMediaMovel(5));

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(NivelAlerta.ALERTA, resultado.get().getNivel());
        verify(alertaDAO).salvar(any(Alerta.class));
    }

    @Test
    void deveEmitirAlertaCriticoQuandoPrevisaoPassarDe150Porcento() throws Exception {
        // Arrange: média 50 contra limiar 30 = 166% -> CRITICO
        List<Medicao> historico = MedicoesDeTeste.de("Chuva", "mm/h", 50, 50, 50, 50, 50);
        when(medicaoDAO.listarPorSensor("Chuva")).thenReturn(historico);

        // Act
        Optional<Alerta> resultado = servico.avaliar("Chuva", "mm/h", new PrevisaoMediaMovel(5));

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(NivelAlerta.CRITICO, resultado.get().getNivel());
        verify(alertaDAO).salvar(any(Alerta.class));
    }

    @Test
    void deveEmitirAlertaDeAtencaoQuandoPrevisaoChegarA80Porcento() throws Exception {
        // Arrange: média 25 contra limiar 30 = 83% -> ATENCAO
        List<Medicao> historico = MedicoesDeTeste.de("Chuva", "mm/h", 25, 25, 25, 25, 25);
        when(medicaoDAO.listarPorSensor("Chuva")).thenReturn(historico);

        // Act
        Optional<Alerta> resultado = servico.avaliar("Chuva", "mm/h", new PrevisaoMediaMovel(5));

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(NivelAlerta.ATENCAO, resultado.get().getNivel());
    }

    @Test
    void naoDeveEmitirAlertaQuandoPrevisaoEstiverNormal() throws Exception {
        // Arrange: média 10 contra limiar 30 = 33% -> NORMAL
        List<Medicao> historico = MedicoesDeTeste.de("Chuva", "mm/h", 10, 10, 10, 10, 10);
        when(medicaoDAO.listarPorSensor("Chuva")).thenReturn(historico);

        // Act
        Optional<Alerta> resultado = servico.avaliar("Chuva", "mm/h", new PrevisaoMediaMovel(5));

        // Assert
        assertFalse(resultado.isPresent());
        verify(alertaDAO, never()).salvar(any(Alerta.class));
    }

    @Test
    void deveFicarEmSilencioQuandoHistoricoForInsuficiente() throws Exception {
        // Arrange: só 3 leituras, e a média móvel pede 5
        List<Medicao> historico = MedicoesDeTeste.de("Chuva", "mm/h", 40, 40, 40);
        when(medicaoDAO.listarPorSensor("Chuva")).thenReturn(historico);

        // Act + Assert
        assertThrows(DadosInvalidosException.class,
                () -> servico.avaliar("Chuva", "mm/h", new PrevisaoMediaMovel(5)));
        verify(alertaDAO, never()).salvar(any(Alerta.class));
    }
}