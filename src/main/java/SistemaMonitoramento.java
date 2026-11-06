import dominio.Medicao;
import excecao.DadosInvalidosException;
import java.time.LocalDateTime;

public class SistemaMonitoramento {
    public static void main(String[] args) {
        try {
            Medicao ok = new Medicao(null, "Chuva", 42.5, "mm/h", LocalDateTime.now());
            System.out.println(ok);

            Medicao ruim = new Medicao(null, "Chuva", -5, "mm/h", LocalDateTime.now());
            System.out.println(ruim);
        } catch (DadosInvalidosException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}