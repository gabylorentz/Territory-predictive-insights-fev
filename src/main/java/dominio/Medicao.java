package dominio;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import excecao.DadosInvalidosException;


public class Medicao {
    private Long id;
    private String tipoSensor;
    private double valor;
    private String unidade;
    private LocalDateTime dataHora;

    public Medicao(Long id, String tipoSensor, double valor, String unidade, LocalDateTime dataHora) throws DadosInvalidosException {
       
        //Todo 1: Validar se os dados de medição são nulos ou inválidos'
        
        if (tipoSensor == null || tipoSensor.isBlank()){
            throw new DadosInvalidosException("Tipo de sensor inválido.");
        }
        
        //Todo 2: Validar se o valor da medição é inválido (NaN))'
        if (Double.isNaN(valor)) {
            throw new DadosInvalidosException("Mediçao de " + tipoSensor + " com valor inválido (NaN).");
        }

        //Todo 3: Validar se o valor da medição é negativo'
        if (valor < 0) {
            throw new DadosInvalidosException("Mediçao de " + tipoSensor + " negativa: " + valor);
    }
        this.id = id;
        this.tipoSensor = tipoSensor;
        this.valor = valor;
        this.unidade = unidade;
        this.dataHora = dataHora;
        }       


    //Todo 4: Implementar os métodos getters para os atributos da classe Medicao
    public double getValor(){
        return valor;
    }

    public Long getId() {
        return id;
    }

    public String getTipoSensor() {
        return tipoSensor;
    }

    public String getUnidade() {
        return unidade;
    }
    public LocalDateTime getDataHora() {
        return dataHora;
    }

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    //Todo 5: Implementar o método toString() para exibir as informações da medição
   @Override
    public String toString() {
    return "[" + dataHora.format(FORMATO) + "] " + tipoSensor + ": " + valor + " " + unidade;
    }
}