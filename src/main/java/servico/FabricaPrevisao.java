package servico;

import dominio.EstrategiaPrevisao;
import dominio.PrevisaoComparacao;
import dominio.PrevisaoMediaMovel;
import excecao.DadosInvalidosException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class FabricaPrevisao {

    private final Map<String, Supplier<EstrategiaPrevisao>> receitas = new LinkedHashMap<>();

    public FabricaPrevisao() {
        ConfiguracaoSistema config = ConfiguracaoSistema.getInstancia();
        receitas.put("media", () -> new PrevisaoMediaMovel(config.getJanelaMediaMovel()));
        receitas.put("comparacao", () -> new PrevisaoComparacao(config.getPeriodoComparacao()));
    
    }

    public EstrategiaPrevisao criar(String tipo) throws DadosInvalidosException {
        Supplier<EstrategiaPrevisao> receita = receitas.get(tipo);
        if (receita == null) {
            throw new DadosInvalidosException("Tipo de previsão desconhecido: " + tipo);
        }
        return receita.get();
    }

    public List<EstrategiaPrevisao> criarTodas() {
        List<EstrategiaPrevisao> lista = new ArrayList<>();
        for (Supplier<EstrategiaPrevisao> receita : receitas.values()) {
            lista.add(receita.get());
        }
        return lista;
    }
}