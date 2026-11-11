package dominio;

import excecao.DadosInvalidosException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class MedicoesDeTeste {

    private MedicoesDeTeste() {
    }

    public static List<Medicao> de(String tipoSensor, String unidade, double... valores)
            throws DadosInvalidosException {
        List<Medicao> lista = new ArrayList<>();
        for (double valor : valores) {
            lista.add(new Medicao(null, tipoSensor, valor, unidade, LocalDateTime.now()));
        }
        return lista;
    }
}