package school.sptech;

import io.github.cdimascio.dotenv.Dotenv;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;


public class Conexao {

    private DataSource conexao;

    public Conexao() {

        Dotenv dotenv = Dotenv.load();

        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl(dotenv.get("DB_URL"));
        basicDataSource.setUsername(dotenv.get("DB_USER"));
        basicDataSource.setPassword(dotenv.get("DB_PASSWORD"));
        basicDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

        this.conexao = basicDataSource;
    }

    public DataSource getConexao() {
        return this.conexao;
    }
}