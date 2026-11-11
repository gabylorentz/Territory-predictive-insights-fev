package servico;

import dominio.Alerta;
import dominio.DadosTerritorio;
import dominio.EstrategiaPrevisao;
import dominio.Medicao;
import dominio.NivelAlerta;
import excecao.DadosInvalidosException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import persistencia.AlertaDAO;
import persistencia.MedicaoDAO;

public class ServicoMonitoramento {

    private final MedicaoDAO medicaoDAO;
    private final AlertaDAO alertaDAO;
    private final DadosTerritorio dadosTerritorio;

    public ServicoMonitoramento(MedicaoDAO medicaoDAO, AlertaDAO alertaDAO,
                                DadosTerritorio dadosTerritorio) {
        this.medicaoDAO = medicaoDAO;
        this.alertaDAO = alertaDAO;
        this.dadosTerritorio = dadosTerritorio;
    }

    public Optional<Alerta> avaliar(String tipoSensor, String unidade, EstrategiaPrevisao estrategia)
            throws SQLException, DadosInvalidosException {

      
        List<Medicao> historico = medicaoDAO.listarPorSensor(tipoSensor);

        double previsto = estrategia.prever(historico);

        double limiar = dadosTerritorio.getLimiar(tipoSensor);

        NivelAlerta nivel = NivelAlerta.classificar(previsto, limiar);

        if (nivel == NivelAlerta.NORMAL) {
            return Optional.empty();
        }

        Alerta alerta = new Alerta(null, tipoSensor, unidade, nivel, previsto, limiar, LocalDateTime.now());
        alertaDAO.salvar(alerta);
        return Optional.of(alerta);
    }
}
   