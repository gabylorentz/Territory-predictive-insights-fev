package persistencia;


import dominio.*;
import excecao.DadosInvalidosException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class AlertaDAO {

    public void salvar(Alerta alerta) throws SQLException{
        String sql = "INSERT INTO alerta (tipo_sensor, unidade, nivel, valor_previsto, limiar, data_hora) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexaoBanco.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, alerta.getTipoSensor());
            ps.setString(2, alerta.getUnidade());
            ps.setString(3, alerta.getNivel().name());
            ps.setDouble(4, alerta.getValorPrevisto());
            ps.setDouble(5, alerta.getLimiar());
            ps.setObject(6, alerta.getDataHora());

            ps.executeUpdate();
        }
    }
    public List<Alerta> listarTodos() throws SQLException, DadosInvalidosException {
        String sql = "SELECT id, tipo_sensor, unidade, nivel, valor_previsto, limiar, data_hora FROM alerta ORDER BY data_hora DESC";
        List<Alerta> lista = new ArrayList<>();

        try (Connection con = ConexaoBanco.getConexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Alerta a = new Alerta(
                        rs.getLong("id"),
                        rs.getString("tipo_sensor"),
                        rs.getString("unidade"),
                        NivelAlerta.valueOf(rs.getString("nivel")),
                        rs.getDouble("valor_previsto"),
                        rs.getDouble("limiar"),
                        rs.getObject("data_hora", LocalDateTime.class));
                lista.add(a);
            }
        }

        return lista;
    }
    
}
