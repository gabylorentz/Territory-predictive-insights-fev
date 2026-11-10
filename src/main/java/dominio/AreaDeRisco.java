package dominio;

import excecao.DadosInvalidosException;

public class AreaDeRisco extends ComponenteTerritorial {

    private int populacaoExposta;   
    public AreaDeRisco(Long id, String nome, String regiao, int populacaoExposta)
            throws DadosInvalidosException {
        super(id, nome, regiao);
        setPopulacaoExposta(populacaoExposta);
    }

    public int getPopulacaoExposta() {
        return populacaoExposta;
    }

    public void setPopulacaoExposta(int populacaoExposta) throws DadosInvalidosException {
    if (populacaoExposta <= 0) {
        throw new DadosInvalidosException("População exposta inválida: " + populacaoExposta);
    }
    this.populacaoExposta = populacaoExposta;
}
    @Override
    public String verificarStatus() {
        return getNome() + " (" + getRegiao() + ") | Status: " + getStatus () + " | ~" + getPopulacaoExposta() + " pessoas expostas";
    }
}