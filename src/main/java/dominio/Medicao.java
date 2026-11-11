package dominio;

import excecao.DadosInvalidosException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Medicao {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final Long id;
    private final String tipoSensor;
    private final double valor;
    private final String unidade;
    private final LocalDateTime dataHora;

    public Medicao(Long id, String tipoSensor, double valor, String unidade,
                   LocalDateTime dataHora) throws DadosInvalidosException {

        if (tipoSensor == null || tipoSensor.isBlank()) {
            throw new DadosInvalidosException("Tipo de sensor da medição não informado.");
        }
        if (unidade == null || unidade.isBlank()) {
            throw new DadosInvalidosException("Unidade da medição de " + tipoSensor + " não informada.");
        }
        if (dataHora == null) {
            throw new DadosInvalidosException("Data e hora da medição de " + tipoSensor + " não informadas.");
        }
        if (Double.isNaN(valor)) {
            throw new DadosInvalidosException("Medição de " + tipoSensor + " com valor inválido (NaN).");
        }
        if (valor < 0) {
            throw new DadosInvalidosException("Medição de " + tipoSensor + " negativa: " + valor);
        }

        this.id = id;
        this.tipoSensor = tipoSensor;
        this.valor = valor;
        this.unidade = unidade;
        this.dataHora = dataHora;
    }

    public Long getId() { return id; }
    public String getTipoSensor() { return tipoSensor; }
    public double getValor() { return valor; }
    public String getUnidade() { return unidade; }
    public LocalDateTime getDataHora() { return dataHora; }

    @Override
    public String toString() {
        return "[" + dataHora.format(FORMATO) + "] " + tipoSensor + ": " + valor + " " + unidade;
    }
}
