package dominio;

public enum NivelAlerta {
    NORMAL("NORMAL", 0.0),
    ATENCAO("ATENÇÃO", 0.8),
    ALERTA("ALERTA", 1.0),
    CRITICO("CRÍTICO", 1.5);

    private final String rotulo;
    private final double fatorMinimo;

    NivelAlerta(String rotulo, double fatorMinimo) {
        this.rotulo = rotulo;
        this.fatorMinimo = fatorMinimo;
    }

    public String getRotulo() {
        return rotulo;
    }

    public double getFatorMinimo() {
        return fatorMinimo;
    }

    public static NivelAlerta classificar(double valorPrevisto, double limiar) {
        double proporcao = valorPrevisto / limiar;
        NivelAlerta[] niveis = values();

        for (int i = niveis.length - 1; i >= 0; i--) {
            if (proporcao >= niveis[i].getFatorMinimo()) {
                return niveis[i];
            }
        }

        return NORMAL;
    }
}