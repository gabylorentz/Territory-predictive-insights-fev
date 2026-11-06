package dominio;

import excecao.DadosInvalidosException;
import excecao.FonteIndisponivelException;
import java.time.LocalDateTime;
import java.util.Random;

public class SensorChuva implements Sensor {

    private final Random random = new Random();

    @Override
    public String getTipo() {
        return "Chuva";
    }

    @Override
    public String getUnidade() {
        return "mm/h";
    }

    @Override
    public Medicao ler() throws DadosInvalidosException, FonteIndisponivelException {
        // 10% de chance de o sensor falhar
        if (random.nextInt(10) == 0){
            throw new FonteIndisponivelException("Sensor de " + getTipo() + " não respondeu.");
        }

        //Valor entre -5 a 55 (O negativo é para testar a validação)
        double valor = random.nextDouble() * 60 - 5;
        valor = Math.round(valor * 10.0) / 10.0;

        return new Medicao(null, getTipo(), valor, getUnidade(), LocalDateTime.now());
    }
    
}
