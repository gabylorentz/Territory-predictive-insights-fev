package dominio;

import excecao.DadosInvalidosException;
import java.util.List;

public class PrevisaoComparacao extends EstrategiaPrevisao {

    private final int periodo;

    public PrevisaoComparacao(int periodo) {
        super("Comparação com período anterior", periodo * 2);
        this.periodo = periodo;
    }

    @Override
    public double prever(List<Medicao> historico) throws DadosInvalidosException {
        super.prever(historico);   // valida o mínimo de leituras (lança exceção se faltar)

        int tamanho = historico.size();
        List<Medicao> recente = historico.subList(tamanho - periodo, tamanho);
        List<Medicao> anterior = historico.subList(tamanho - 2 * periodo, tamanho - periodo);

        double tendencia = media(recente) - media(anterior);

        return arredondar(Math.max(0, media(recente) + tendencia));
    }

    @Override
    public String explicar(double valorPrevisto, String unidade) {
        return "Comparando as últimas " + periodo + " leituras com as " + periodo
                + " anteriores, a tendência indica cerca de " + valorPrevisto + " " + unidade + ".";
    }
}