package dominio;

import java.util.List;
import excecao.DadosInvalidosException;

public abstract class EstrategiaPrevisao {
    
    private final String nome;
    private final int minimoLeituras;

    protected EstrategiaPrevisao(String nome, int minimoLeituras) {
        this.nome = nome;
        this.minimoLeituras = minimoLeituras;
    }

    public double prever(List<Medicao> historico) throws DadosInvalidosException {
    int quantidade = (historico == null) ? 0 : historico.size();
    if (quantidade < minimoLeituras) {
        throw new DadosInvalidosException("Histórico insuficiente para " + nome + ": "
                + quantidade + " leitura(s), mínimo de " + minimoLeituras + ".");
    }
    return arredondar(media(historico));   
}

protected double arredondar(double valor) {
    return Math.round(valor * 10) / 10.0;
}
    public abstract String explicar(double valorPrevisto, String unidade);

    protected double media(List<Medicao> medicoes) {
        double soma = 0;
        for (Medicao m : medicoes) {
            soma += m.getValor();
        }
        return soma / medicoes.size();
    }

    public String getNome() {
        return nome;
    }

    public int getMinimoLeituras() {
        return minimoLeituras;
    }
}
