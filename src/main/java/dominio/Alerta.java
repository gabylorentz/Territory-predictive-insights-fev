package dominio;

import excecao.DadosInvalidosException;
import java.time.LocalDateTime;

public class Alerta {

    private final Long id;
    private final String tipoSensor;
    private final String unidade;
    private final NivelAlerta nivel;
    private final double valorPrevisto;
    private final double limiar;
    private final LocalDateTime dataHora;

    public Alerta(Long id, String tipoSensor, String unidade, NivelAlerta nivel,
                  double valorPrevisto, double limiar, LocalDateTime dataHora)
            throws DadosInvalidosException {

        if (tipoSensor == null || tipoSensor.isBlank()) {
            throw new DadosInvalidosException("Tipo de sensor do alerta não informado.");
        }
        if (unidade == null || unidade.isBlank()) {
            throw new DadosInvalidosException("Unidade do alerta não informada para " + tipoSensor + ".");
        }
        if (nivel == null || nivel == NivelAlerta.NORMAL) {
            throw new DadosInvalidosException("Nível inválido para alerta de " + tipoSensor + ": " + nivel);
        }
        if (Double.isNaN(valorPrevisto) || valorPrevisto < 0) {
            throw new DadosInvalidosException("Valor previsto inválido para " + tipoSensor + ": " + valorPrevisto);
        }
        if (Double.isNaN(limiar) || limiar <= 0) {
            throw new DadosInvalidosException("Limiar inválido para " + tipoSensor + ": " + limiar);
        }
        if (dataHora == null) {
            throw new DadosInvalidosException("Data e hora do alerta de " + tipoSensor + " não informadas.");
}

        this.id = id;
        this.tipoSensor = tipoSensor;
        this.unidade = unidade;
        this.nivel = nivel;
        this.valorPrevisto = valorPrevisto;
        this.limiar = limiar;
        this.dataHora = dataHora;
    }

    public Long getId() { return id; }
    public String getTipoSensor() { return tipoSensor; }
    public String getUnidade() { return unidade; }
    public NivelAlerta getNivel() { return nivel; }
    public double getValorPrevisto() { return valorPrevisto; }
    public double getLimiar() { return limiar; }
    public LocalDateTime getDataHora() { return dataHora; }

    public String getMensagem() {
        long percentual = Math.round(valorPrevisto / limiar * 100);
        return "[" + nivel.getRotulo() + "] " + tipoSensor + ": previsto " + valorPrevisto + " " + unidade
                + ", limiar de " + limiar + " " + unidade + " (" + percentual + "% do limite)";
    }
}