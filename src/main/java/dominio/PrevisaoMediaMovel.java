package dominio;

import java.util.List;

import excecao.DadosInvalidosException;

public class PrevisaoMediaMovel extends EstrategiaPrevisao {

    private final int janela;

    public PrevisaoMediaMovel(int janela) {
        super("Média móvel", janela);
        this.janela = janela;
    }

    @Override
    public double prever(List<Medicao> historico) throws DadosInvalidosException {
        super.prever(historico);   // valida o mínimo de leituras (lança exceção se faltar)
        List<Medicao> ultimas = historico.subList(historico.size() - janela, historico.size());
        return arredondar(media(ultimas));
        }

    @Override
    public String explicar(double valorPrevisto, String unidade) {
        return "Média das últimas " + janela + " leituras: espera-se cerca de "
                + valorPrevisto + " " + unidade + " na próxima medição.";
    }
}