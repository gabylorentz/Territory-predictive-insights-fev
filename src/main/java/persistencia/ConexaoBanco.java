package persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexaoBanco {
    private static final String URL = "jdbc:h2:./dados/monitoramento"; // Caminho para o banco de dados
    private static final String USUARIO = "sa"; // Usuário padrão do H2
    private static final String SENHA = ""; // Senha padrão do H2
    
    private ConexaoBanco() {
    }

    public static Connection getConexao() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }

    public static void criarTabelas() throws SQLException {

        String sql = """ 
                    CREATE TABLE IF NOT EXISTS Medicao (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    tipo_Sensor VARCHAR(255) NOT NULL, 
                    valor DOUBLE NOT NULL,
                    unidade VARCHAR(50) NOT NULL,
                    data_Hora TIMESTAMP NOT NULL)""";
            
            try (Connection conn = getConexao(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }

    }
}
