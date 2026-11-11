package dominio;

import excecao.DadosInvalidosException;
import java.util.HashMap;
import java.util.Map;

public class DadosTerritorio {

    private final Map<String, Double> limiares = new HashMap<>(); 

    public DadosTerritorio() {
        // Inicializa os limiares com valores padrão
        limiares.put("Chuva", 30.0); // Limiar de chuva em mm/h
        limiares.put("Nível do Córrego", 200.0); // Limiar de nível do córrego em cm
        limiares.put("Umidade do Solo", 85.0); // Limiar de umidade em %
    }

    public double getLimiar(String tipoSensor) throws DadosInvalidosException {
        Double limiar = limiares.get(tipoSensor);
        if (limiar == null) {
            throw new DadosInvalidosException("Limiar não encontrado para o sensor: " + tipoSensor);
        }
        return limiar;
    }

    public void setLimiar(String tipoSensor, double valor) throws DadosInvalidosException {
        if (!limiares.containsKey(tipoSensor)) {
            throw new DadosInvalidosException("Sensor sem limiar cadastrado: " + tipoSensor);
        }
        if (valor <= 0 || Double.isNaN(valor)) {
            throw new DadosInvalidosException("Valor do limiar inválido: " + tipoSensor + ": " + valor);
        }
        limiares.put(tipoSensor, valor);
    }
}
