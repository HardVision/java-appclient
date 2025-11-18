package school.sptech;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;


public class Conexao {

    private DataSource conexao;

    public Conexao() {

        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl("jdbc:mysql://localhost:3306/hardvision?useSSL=false&serverTimezone=UTC");
        basicDataSource.setUsername("root");
        basicDataSource.setPassword("meshnapearlcave2412");
        basicDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");


        this.conexao = basicDataSource;
    }

    public DataSource getConexao() {
        return this.conexao;
    }
}