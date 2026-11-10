package dominio;

import java.util.List;

public class PrevisaoComparacao extends EstrategiaPrevisao {

    private final int periodo;

    public PrevisaoComparacao(int periodo) {
       
        super("Comparação com período anterior", periodo * 2);
        this.periodo = periodo;
    }

    @Override
    protected double calcular(List<Medicao> historico) {
        int tamanho = historico.size();

        List<Medicao> recente = historico.subList(tamanho - periodo, tamanho);

        List<Medicao> anterior = historico.subList(tamanho - 2 * periodo, tamanho - periodo);

        double tendencia = media(recente) - media(anterior);

        return Math.max(0, media(recente) + tendencia);
    }

    @Override
    public String explicar(double valorPrevisto, String unidade) {

        return "Comparando as últimas " + periodo + " leituras com as " + periodo
                + " anteriores, a tendência indica cerca de " + valorPrevisto + " " + unidade + ".";
    }
}