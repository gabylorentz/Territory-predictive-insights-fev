import dominio.*;
import excecao.DadosInvalidosException;
import excecao.FonteIndisponivelException;
import java.util.List;

public class SistemaMonitoramento {
    public static void main(String[] args) {

        // ===== ETAPA 3: território e limiares =====
        System.out.println("=== Teste do território ===");
        try {
            // Teste 1: área de risco válida
            ComponenteTerritorial area = new AreaDeRisco(null, "Margem do Córrego", "Zona Sul", 1200);
            System.out.println(area.verificarStatus());

            // Teste 2: alterar e ler um limiar válido
            DadosTerritorio dados = new DadosTerritorio();
            dados.setLimiar("Chuva", 40.0);
            System.out.println("Limiar de chuva: " + dados.getLimiar("Chuva"));

            // Teste 3: limiar inválido (deve cair no catch)
            dados.setLimiar("Chuva", -10);

            // Teste 4: sensor inexistente (comente o Teste 3 e descomente este)
            // dados.setLimiar("Chuvaa", 40.0);

        } catch (DadosInvalidosException e) {
            System.out.println("[AVISO] " + e.getMessage());
        }

        // ===== ETAPA 2: leitura dos sensores =====
        List<Sensor> sensores = List.of(
                new SensorChuva(),
                new SensorNivelCorrego(),
                new SensorUmidadeSolo()
        );

        for (int rodada = 1; rodada <= 3; rodada++) {
            System.out.println("--- Rodada " + rodada + " ---");
            for (Sensor sensor : sensores) {
                try {
                    System.out.println(sensor.ler());
                } catch (DadosInvalidosException e) {
                    System.out.println("[AVISO] Dado inválido: " + e.getMessage());
                } catch (FonteIndisponivelException e) {
                    System.out.println("[FALHA] Sensor indisponível: " + e.getMessage());
                }
            }
        }
    }
}