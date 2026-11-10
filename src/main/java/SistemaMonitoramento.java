import dominio.*;
import excecao.DadosInvalidosException;
import excecao.FonteIndisponivelException;
import java.util.List;
import persistencia.ConexaoBanco;
import persistencia.MedicaoDAO;
import java.sql.SQLException;

public class SistemaMonitoramento {
    public static void main(String[] args) {

        // ===== ETAPA 4: leitura + gravação no banco =====
        MedicaoDAO medicaoDAO = new MedicaoDAO();

        try {
            ConexaoBanco.criarTabelas();
        } catch (SQLException e) {
            System.out.println("[FALHA] Não foi possível preparar o banco: " + e.getMessage());
            return;
        }

        List<Sensor> sensores = List.of(
                new SensorChuva(),
                new SensorNivelCorrego(),
                new SensorUmidadeSolo());

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
                // ===== ETAPA 5: previsões =====
        System.out.println("--- Previsões para Chuva ---");
        List<EstrategiaPrevisao> estrategias = List.of(
                new PrevisaoMediaMovel(5),
                new PrevisaoComparacao(3)
        );

        try {
            List<Medicao> historico = medicaoDAO.listarPorSensor("Chuva");
            System.out.println("Leituras no histórico: " + historico.size());

            for (EstrategiaPrevisao estrategia : estrategias) {
                try {
                    double previsto = estrategia.prever(historico);
                    System.out.println("[" + estrategia.getNome() + "] " + estrategia.explicar(previsto, "mm/h"));
                } catch (DadosInvalidosException e) {
                    System.out.println("[SILÊNCIO] " + e.getMessage());
                }
            }
        } catch (SQLException | DadosInvalidosException e) {

            System.out.println("[FALHA] Erro ao ler histórico: " + e.getMessage());}
            }
        }