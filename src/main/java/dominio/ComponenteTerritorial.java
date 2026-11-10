package dominio;

import excecao.DadosInvalidosException;

public abstract class ComponenteTerritorial {

    private Long id;
    private String nome;
    private String regiao;
    private String status;

    protected ComponenteTerritorial(Long id, String nome, String regiao) throws DadosInvalidosException {
        if (nome == null || nome.isBlank()){
            throw new DadosInvalidosException("Nome do componente não informado.");
        }
        if (regiao == null || regiao.isBlank()) {
            throw new DadosInvalidosException("Região do componente não informada.");
        }
    
        this.id = id;
        this.nome = nome;
        this.regiao = regiao;
        this.status = "NORMAL";
    }

    public abstract String verificarStatus();

    public Long getId() {
        return id;
    }
    public String getNome() {
        return nome;
    }
    public String getRegiao() {
        return regiao;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) throws DadosInvalidosException {
        if (status == null || status.isBlank()) {
            throw new DadosInvalidosException("Status Inválido.");
        }
        this.status = status;

    }
    
}
