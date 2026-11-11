package persistencia;

import dominio.Medicao;
import excecao.DadosInvalidosException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MedicaoDAO {

    public void salvar(Medicao medicao) throws SQLException {
        String sql = "INSERT INTO medicao (tipo_sensor, valor, unidade, data_hora) VALUES (?, ?, ?, ?)";

        try (Connection con = ConexaoBanco.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, medicao.getTipoSensor());
            ps.setDouble(2, medicao.getValor());
            ps.setString(3, medicao.getUnidade());
            ps.setObject(4, medicao.getDataHora());

            ps.executeUpdate();
        }
    }

   public List<Medicao> listarPorSensor(String tipoSensor) throws SQLException, DadosInvalidosException {
    String sql = """
    SELECT id, tipo_Sensor, valor, unidade, data_Hora FROM medicao WHERE tipo_Sensor = ? ORDER BY data_Hora""";
    List<Medicao> lista = new ArrayList<>();

   
    try (Connection con = ConexaoBanco.getConexao();
         PreparedStatement ps = con.prepareStatement(sql)) {

        
        ps.setString(1, tipoSensor);

    
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Medicao m = new Medicao(
                        rs.getLong("id"),
                        rs.getString("tipo_sensor"),
                        rs.getDouble("valor"),
                        rs.getString("unidade"),
                        rs.getObject("data_hora", LocalDateTime.class));
                lista.add(m);
            }
        }
    }

    return lista;
}

}