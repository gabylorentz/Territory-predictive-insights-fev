package dominio;

import java.util.List;

public class PrevisaoMediaMovel extends EstrategiaPrevisao {

    private final int leitura;

    public PrevisaoMediaMovel(int leitura) {
        super("Média móvel", leitura);
        this.leitura = leitura;
    }

    @Override
    protected double calcular(List<Medicao> historico) {
        List<Medicao> ultimas = historico.subList(historico.size() - leitura, historico.size());
        return media(ultimas);
    }

    @Override
    public String explicar(double valorPrevisto, String unidade) {
        return "Média das últimas " + leitura + " leituras: espera-se cerca de "
                + valorPrevisto + " " + unidade + " na próxima medição.";
    }
}