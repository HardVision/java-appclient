package school.sptech;

import io.github.cdimascio.dotenv.Dotenv;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;


public class Conexao {

    private DataSource conexao;

    public Conexao() {

        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl(System.getenv("DB_URL"));
        basicDataSource.setUsername(System.getenv("DB_USER"));
        basicDataSource.setPassword(System.getenv("DB_PASS"));
        basicDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

        this.conexao = basicDataSource;
    }

    public DataSource getConexao() {
        return this.conexao;
    }
}