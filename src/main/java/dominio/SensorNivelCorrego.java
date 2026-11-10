package dominio;

import excecao.DadosInvalidosException;
import excecao.FonteIndisponivelException;
import java.time.LocalDateTime;
import java.util.Random;

public class SensorNivelCorrego implements Sensor {

    private final Random random = new Random();

    @Override
    public String getTipo() {
        return "Nível do Córrego";
    }

    @Override
    public String getUnidade() {
        return "cm";
    }

    // 10% de chance de o sensor estar com falha
    @Override
    public boolean estaFuncionando() {
        return random.nextInt(10) != 0;
    }

    @Override
    public Medicao ler() throws DadosInvalidosException, FonteIndisponivelException {
        // 10% de chance de o sensor falhar
        if (!estaFuncionando()) {
            throw new FonteIndisponivelException("Sensor de " + getTipo() + " não está funcionando.");
        }

        //Valor entre -10 a 290 (O negativo é para testar a validação)
        double valor = random.nextDouble() * 300 - 10;
        valor = Math.round(valor * 10.0) / 10.0;

        return new Medicao(null, getTipo(), valor, getUnidade(), LocalDateTime.now());
    }
    
}

