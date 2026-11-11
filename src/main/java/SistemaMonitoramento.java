import dominio.*;
import excecao.DadosInvalidosException;
import excecao.FonteIndisponivelException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import persistencia.AlertaDAO;
import persistencia.ConexaoBanco;
import persistencia.MedicaoDAO;
import servico.ConfiguracaoSistema;
import servico.FabricaPrevisao;
import servico.ServicoMonitoramento;

public class SistemaMonitoramento {
    public static void main(String[] args) {

        // ===== ETAPA 4: banco + leitura + gravação =====
        MedicaoDAO medicaoDAO = new MedicaoDAO();
        AlertaDAO alertaDAO = new AlertaDAO();

        try {
            ConexaoBanco.criarTabelas();
        } catch (SQLException e) {
            System.out.println("[FALHA] Não foi possível preparar o banco: " + e.getMessage());
            return;
        }

        List<Sensor> sensores = List.of(
                new SensorChuva(),
                new SensorNivelCorrego(),
                new SensorUmidadeSolo()
        );

        System.out.println("--- Leitura dos sensores ---");
        for (Sensor sensor : sensores) {
            try {
                Medicao medicao = sensor.ler();
                medicaoDAO.salvar(medicao);
                System.out.println("[OK] Gravada: " + medicao);
            } catch (DadosInvalidosException e) {
                System.out.println("[AVISO] Dado inválido: " + e.getMessage());
            } catch (FonteIndisponivelException e) {
                System.out.println("[FALHA] Sensor indisponível: " + e.getMessage());
            } catch (SQLException e) {
                System.out.println("[FALHA] Erro ao gravar no banco: " + e.getMessage());
            }
        }

        System.out.println("--- Histórico de Chuva no banco ---");
        try {
            for (Medicao m : medicaoDAO.listarPorSensor("Chuva")) {
                System.out.println(m);
            }
        } catch (SQLException | DadosInvalidosException e) {
            System.out.println("[FALHA] Erro ao ler histórico: " + e.getMessage());
        }

        // ===== ETAPA 5 e 6: previsões (estratégias vêm da fábrica) =====
        FabricaPrevisao fabrica = new FabricaPrevisao();
        List<EstrategiaPrevisao> estrategias = fabrica.criarTodas();

        System.out.println("--- Previsões para Chuva ---");
        try {
            List<Medicao> historico = medicaoDAO.listarPorSensor("Chuva");
            System.out.println("Leituras no histórico: " + historico.size());

            for (EstrategiaPrevisao estrategia : estrategias) {
                try {
                    double previsto = estrategia.prever(historico);
                    System.out.println("[" + estrategia.getNome() + "] "
                            + estrategia.explicar(previsto, "mm/h"));
                } catch (DadosInvalidosException e) {
                    System.out.println("[SILÊNCIO] " + e.getMessage());
                }
            }
        } catch (SQLException | DadosInvalidosException e) {
            System.out.println("[FALHA] Erro ao ler histórico: " + e.getMessage());
        }

        // ===== ETAPA 7: alertas =====
        System.out.println("--- Alertas ---");
        ServicoMonitoramento servico = new ServicoMonitoramento(medicaoDAO, alertaDAO,
                ConfiguracaoSistema.getInstancia().getDadosTerritorio());


        for (Sensor sensor : sensores) {
            for (EstrategiaPrevisao estrategia : estrategias) {
                try {
                    Optional<Alerta> alerta = servico.avaliar(
                            sensor.getTipo(), sensor.getUnidade(), estrategia);

                    if (alerta.isPresent()) {
                        System.out.println(alerta.get().getMensagem());
                    } else {
                        System.out.println("[NORMAL] " + sensor.getTipo()
                                + " (" + estrategia.getNome() + ")");
                    }
                } catch (DadosInvalidosException e) {
                    System.out.println("[SILÊNCIO] " + e.getMessage());
                } catch (SQLException e) {
                    System.out.println("[FALHA] Erro no banco: " + e.getMessage());
                }
            }
        }

        System.out.println("--- Alertas já emitidos ---");
        try {
            for (Alerta a : alertaDAO.listarTodos()) {
                System.out.println(a.getMensagem());
            }
        } catch (SQLException | DadosInvalidosException e) {
            System.out.println("[FALHA] Erro ao listar alertas: " + e.getMessage());
        }
    }
}