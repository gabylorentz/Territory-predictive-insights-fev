import dominio.*;
import excecao.DadosInvalidosException;
import excecao.FonteIndisponivelException;
import java.util.List;

public class SistemaMonitoramento {
    public static void main(String[] args) {
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
                    System.out.println("⚠ Dado inválido: " + e.getMessage());
                } catch (FonteIndisponivelException e) {
                    System.out.println("✖ Sensor indisponível: " + e.getMessage());
                }
            }
        }
    }
}