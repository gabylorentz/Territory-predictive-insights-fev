import dominio.*;
import excecao.DadosInvalidosException;
import java.sql.SQLException;
import java.util.List;
import persistencia.AlertaDAO;
import persistencia.ConexaoBanco;
import persistencia.MedicaoDAO;
import servico.ConfiguracaoSistema;
import servico.FabricaPrevisao;
import servico.MenuConsole;
import servico.ServicoMonitoramento;

public class SistemaMonitoramento {
    public static void main(String[] args) {

        try {
            ConexaoBanco.criarTabelas();
        } catch (SQLException e) {
            System.out.println("[FALHA] Não foi possível preparar o banco: " + e.getMessage());
            return;
        }

        MedicaoDAO medicaoDAO = new MedicaoDAO();
        AlertaDAO alertaDAO = new AlertaDAO();
        DadosTerritorio dados = ConfiguracaoSistema.getInstancia().getDadosTerritorio();
        ServicoMonitoramento servico = new ServicoMonitoramento(medicaoDAO, alertaDAO, dados);

        List<Sensor> sensores = List.of(
                new SensorChuva(),
                new SensorNivelCorrego(),
                new SensorUmidadeSolo()
        );

        try {
            AreaDeRisco area = new AreaDeRisco(null, "Margem do Córrego", "Zona Sul", 1200);
            MenuConsole menu = new MenuConsole(sensores, medicaoDAO, alertaDAO,
                    servico, new FabricaPrevisao(), dados, area);
            menu.iniciar();
        } catch (DadosInvalidosException e) {
            System.out.println("[FALHA] Não foi possível iniciar o território: " + e.getMessage());
        }
    }
}