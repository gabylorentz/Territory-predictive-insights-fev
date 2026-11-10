package servico;

import dominio.DadosTerritorio;

public class ConfiguracaoSistema {

    private static ConfiguracaoSistema instancia;

    private final DadosTerritorio dadosTerritorio;
    private final int janelaMediaMovel;
    private final int periodoComparacao;

    private ConfiguracaoSistema() {
        this.dadosTerritorio = new DadosTerritorio();
        this.janelaMediaMovel = 5;
        this.periodoComparacao = 3;
    }

    public static ConfiguracaoSistema getInstancia() {
        if (instancia == null) {
            instancia = new ConfiguracaoSistema();
        }
        return instancia;
    }

    public DadosTerritorio getDadosTerritorio() {
        return dadosTerritorio;
    }

    public int getJanelaMediaMovel() {
        return janelaMediaMovel;
    }

    public int getPeriodoComparacao() {
        return periodoComparacao;
    }
}