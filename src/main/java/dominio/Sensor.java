package dominio;

import excecao.DadosInvalidosException;
import excecao.FonteIndisponivelException;

public interface Sensor {
    String getTipo();
    String getUnidade();
    Medicao ler () throws DadosInvalidosException, FonteIndisponivelException;
}
