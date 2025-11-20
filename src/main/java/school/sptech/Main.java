package school.sptech;

import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.rede.Rede;
import com.github.britooo.looca.api.group.rede.RedeInterface;
import com.github.britooo.looca.api.util.Conversor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class Main {

    private static long bytesRecebidosAnterior = 0;
    private static long bytesEnviadosAnterior = 0;
    private static long tempoAnteriorMs = System.currentTimeMillis();
    private static int leituraAtual = 0;

    private static final Integer METRICA_DOWNLOAD_ID = 1;
    private static  Integer ALERTA_DOWNLOAD_ID = null;

    private static final Integer METRICA_UPLOAD_ID = 2;
    private static  Integer ALERTA_UPLOAD_ID = null;


    private static final Integer METRICA_THROUGHPUT_ID = 3;
    private static  Integer ALERTA_THROUGHPUT_ID = null;

    private static final Integer METRICA_LATENCIA_ID = 4;
    private static  Integer ALERTA_LATENCIA_ID = null;

    private static Integer buscarOuCadastrarId(JdbcTemplate template, String selectSql, String insertSql, String valorBusca, Object... insertParams) {

        List<Integer> ids = template.query(
                selectSql,
                new Object[]{valorBusca},
                (rs, rowNum) -> rs.getInt(1)
        );

        if (!ids.isEmpty()) {
            return ids.get(0);
        } else {
            System.out.println(">>> Cadastrando novo registro: " + valorBusca + "...");
            try {
                template.update(insertSql, insertParams);

                ids = template.query(
                        selectSql,
                        new Object[]{valorBusca},
                        (rs, rowNum) -> rs.getInt(1)
                );
                return ids.isEmpty() ? null : ids.get(0);

            } catch (DataAccessException e) {
                System.err.println(">>> Erro ao cadastrar " + valorBusca + ". \nCausa: " + e.getMessage());
                return null;
            }
        }
    }

    private static Double buscarMetrica(JdbcTemplate template, String selectSql, Integer valorBusca){
        List<Double> min = template.query(
                selectSql,
                new Object[]{valorBusca},
                (rs, rowNum) -> rs.getDouble(1)
        );

        return min.get(0);
    }

    public static void main(String[] args) {

        Looca looca = new Looca();
        Rede rede = looca.getRede();

        List<RedeInterface> interfaces = rede.getGrupoDeInterfaces().getInterfaces();

        Conexao conexao = new Conexao();
        JdbcTemplate template = new JdbcTemplate(conexao.getConexao());

        System.out.println("API INICIADA - CONECTANDO COM O BANCO DE DADOS HARDVISION\n\n");

        String scriptSQL = """
            CREATE TABLE IF NOT EXISTS endereco (
                idEndereco INT PRIMARY KEY auto_increment,
                rua VARCHAR(45) NOT NULL,
                numero VARCHAR(45) NOT NULL,
                logradouro VARCHAR(45) NOT NULL,
                cidade VARCHAR(45) NOT NULL,
                uf VARCHAR(2) NOT NULL,
                cep CHAR(8) NOT NULL,
                complemento VARCHAR(45)
            );

            CREATE TABLE IF NOT EXISTS empresa (
                idEmpresa INT PRIMARY KEY auto_increment,
                razaoSocial VARCHAR(45) NOT NULL,
                nomeFantasia varchar(45) not null,
                cnpj CHAR(14) NOT NULL UNIQUE,
                fkEndereco INT,
                token char(5),
                FOREIGN KEY (fkEndereco) REFERENCES endereco(idEndereco)
            );

            CREATE TABLE IF NOT EXISTS sistemaOperacional (
                idSistema INT PRIMARY KEY auto_increment,
                tipo VARCHAR(45) NOT NULL,
                versao VARCHAR(50) NOT NULL,
                distribuicao varchar(45) not NULL
            );

            CREATE TABLE IF NOT EXISTS maquina (
                idMaquina INT auto_increment,
                fkEmpresa INT,
                fkSistema INT,
                constraint pkMaq PRIMARY KEY (idMaquina, fkSistema),
                macAddress char(17) NOT NULL UNIQUE,
                localizacao varchar(45),
                FOREIGN KEY (fkEmpresa) REFERENCES empresa(idEmpresa),
                FOREIGN KEY (fkSistema) REFERENCES sistemaOperacional(idSistema)
            );

            CREATE TABLE IF NOT EXISTS tipo (
                idTipo INT PRIMARY KEY auto_increment,
                permissao VARCHAR(45) NOT NULL
            );

            CREATE TABLE IF NOT EXISTS usuario (
                idUsuario INT auto_increment,
                nome VARCHAR(45) NOT NULL,
                email VARCHAR(45) NOT NULL UNIQUE,
                senha VARCHAR(45) NOT NULL,
                cpf char(11) not null unique,
                telefone char(13) not null unique,
                fkEmpresa INT,
                fkTipo int,
                CONSTRAINT pkUsu primary key (idUsuario, fkEmpresa),
                FOREIGN KEY (fkEmpresa) REFERENCES empresa(idEmpresa),
                FOREIGN KEY (fkTipo) REFERENCES tipo(idTipo)
            );

            CREATE TABLE IF NOT EXISTS metricaComponente (
                idMetrica INT PRIMARY KEY auto_increment,
                nome varchar(45) not null,
                medida varchar(45) not NULL,
                min float not null,
                max float not null
            );

            CREATE TABLE IF NOT EXISTS metricaRede (
                idMetricaRede INT PRIMARY KEY auto_increment,
                nome VARCHAR(45) NOT NULL,
                medida varchar(45) not NULL,
                min FLOAT NOT NULL,
                max float not NULL
            );

            CREATE TABLE IF NOT EXISTS componenteRede (
                idComponenteRede INT auto_increment,
                nome varchar(45) not null,
                interfaceRede varchar(45) not null,
                fkMetricaRede int,
                constraint pkCompRede PRIMARY key (idComponenteRede, fkMetricaRede),
                FOREIGN KEY (fkMetricaRede) REFERENCES metricaRede(idMetricaRede)
            );

            CREATE TABLE IF NOT EXISTS componente (
                idComponente INT auto_increment,
                tipo VARCHAR(45) NOT NULL,
                modelo varchar(45) NOT NULL,
                fabricante VARCHAR(45) NOT NULL,
                capacidade varchar(45) NOT NULL,
                fkMetrica int,
                CONSTRAINT pkComp PRIMARY KEY (idComponente, fkMetrica),
                FOREIGN KEY (fkMetrica) REFERENCES metricaComponente(idMetrica)
            );

            CREATE TABLE IF NOT EXISTS alertaComponente (
                idAlerta INT auto_increment,
                fkMetrica INT,
                constraint pkAlertaComp primary key(idAlerta, fkMetrica),
                dtHora timestamp default current_timestamp(),
                estado varchar(45) not NULL,
                FOREIGN KEY (fkMetrica) REFERENCES metricaComponente(idMetrica)
            );

            CREATE TABLE IF NOT EXISTS alertaRede (
                idAlertaRede INT auto_increment,
                fkMetricaRede INT,
                constraint pkAlertaCompRede primary key(idAlertaRede, fkMetricaRede),
                dtHora timestamp default current_timestamp(),
                estado varchar(45) not NULL,
                FOREIGN KEY (fkMetricaRede) REFERENCES metricaRede(idMetricaRede)
            );

            CREATE TABLE IF NOT EXISTS logMonitoramento (
                idMonitoramento INT auto_increment,
                fkMaquina int,
                fkComponente int,
                fkMetrica int,
                fkAlerta int,
                constraint pkLogMonitoramento
                primary key(idMonitoramento,
                fkMaquina, fkComponente, fkMetrica),
                valor INT NOT NULL,
                descricao VARCHAR(255) NOT NULL,
                dtHora timestamp default CURRENT_TIMESTAMP(),
                FOREIGN KEY (fkMaquina) REFERENCES maquina(idMaquina),
                FOREIGN KEY (fkComponente) REFERENCES componente(idComponente),
                FOREIGN KEY (fkMetrica) REFERENCES metricaComponente(idMetrica),
                FOREIGN KEY (fkAlerta) REFERENCES alertaComponente(idAlerta)
                );

            CREATE TABLE IF NOT EXISTS logMonitoramentoRede (
                idMonitoramentoRede INT auto_increment,
                fkAlertaRede INT,
                fkComponenteRede int,
                fkMaquina int,
                fkMetricaRede int ,
                constraint pkLogMonitoramentoRede
                primary key(idMonitoramentoRede,
                fkMaquina, fkComponenteRede, fkAlertaRede),
                ipv4 char(15) NOT NULL,
                throughput decimal(10,2) NOT NULL,
                mbEnviados decimal(10,2) not NULL,
                mbRecebidos decimal(10,2) not NULL,
                pacotesEnviados decimal(10,2) not null,
                pacotesRecebidos decimal(10,2)not null,
                dtHora timestamp default CURRENT_TIMESTAMP(),
                FOREIGN KEY (fkMaquina) REFERENCES maquina(idMaquina),
                FOREIGN KEY (fkComponenteRede) REFERENCES componenteRede(idComponenteRede),
                FOREIGN KEY (fkMetricaRede) REFERENCES metricaRede(idMetricaRede),
                FOREIGN KEY (fkAlertaRede) REFERENCES alertaRede(idAlertaRede)
            );
            """;

        try {
            for (String comando : scriptSQL.split(";")) {
                if (!comando.trim().isEmpty()) {
                    template.execute(comando.trim());
                }
            }
            System.out.println(">>> Estrutura do banco de dados criada com sucesso.");
        } catch (DataAccessException e) {
            System.err.println(">>> Erro ao criar a estrutura do banco. \nVerificar as FK's e a sintaxe.");
            e.printStackTrace();
            return;
        }

        String scriptDML = """
-- ===========================================================
-- INSERÇÃO DE DADOS DE EXEMPLO
-- ===========================================================

INSERT IGNORE INTO endereco (cep, cidade, logradouro, numero, uf, complemento) VALUES
('12345678', 'São Paulo', 'Rua das Flores', '123', 'SP', 'Apto 101'),
('23456789', 'Rio de Janeiro', 'Rua da Paz', '456', 'RJ', 'Casa 10'),
('34567890', 'Belo Horizonte', 'Avenida Brasil', '789', 'MG', NULL);

INSERT IGNORE INTO empresa (fkEndereco, razaoSocial, nomeFantasia, cnpj, token) VALUES
(1, 'Empresa A LTDA', 'Empresa A', '12345678000199', 'ABCDE'),
(2, 'Empresa B S.A.', 'Empresa B', '98765432000110', 'XYZ12'),
(3, 'Empresa C LTDA', 'Empresa C', '19283746000188', 'PQR34');

INSERT IGNORE INTO tipo (permissao) VALUES
('Admin'),
('Membro');

INSERT IGNORE INTO usuario (fkEmpresa, fkTipo, nome, email, senha, cpf, telefone) VALUES
(1, 1, 'João Silva', 'joao@empresaA.com', 'senha123', '11122233344', '11988887777'),
(2, 2, 'Maria Souza', 'maria@empresaB.com', 'senha456', '55566677788', '21999998888'),
(3, 2, 'Pedro Lima', 'pedro@empresaC.com', 'senha789', '99900011122', '31977776666');

INSERT IGNORE INTO sistemaOperacional (tipo, versao, distribuicao) VALUES
('Linux', '20.04', 'Ubuntu'),
('Windows', '11 Pro', 'Microsoft'),
('MacOS', '13.0', 'Apple');

INSERT IGNORE INTO maquina (fkEmpresa, fkSistema, macAddress, localizacao) VALUES
(1, 1, '00:1A:2B:3C:4D:5E', 'Sala 101'),
(2, 2, '00:5F:6A:7B:8C:9D', 'Sala 202'),
(3, 3, '01:2A:3B:4C:5D:6E', 'Sala 303');

INSERT IGNORE INTO maquina (fkEmpresa, fkSistema, macAddress, localizacao) VALUES
(1, 1, '00:1A:2B:3C:4D:6E', 'Sala 105');

INSERT IGNORE INTO metricaComponente (fkEmpresa, nome, medida, min, max) VALUES
(1, 'Uso de CPU', '%', 20, 85),
(1, 'Uso de Memória', '%', 10, 75),
(1, 'Uso de Disco', '%', 0, 90);

INSERT IGNORE INTO componente (tipo, modelo, fabricante, capacidade, fkMetrica) VALUES
('RAM', 'Vengeance LPX', 'Corsair', '16GB', 2),
('Disco', '978 EVO Plus', 'Samsung', '1TB', 3),
('CPU Núcleo 1', 'Ryzen 5 5600X', 'AMD', '3.7GHz', 1),
('CPU Núcleo 2', 'Ryzen 5 5600X', 'AMD', '3.7GHz', 1),
('CPU Núcleo 3', 'Ryzen 5 5600X', 'AMD', '3.7GHz', 1),
('CPU Núcleo 4', 'Ryzen 5 5600X', 'AMD', '3.7GHz', 1),
('CPU Núcleo 5', 'Ryzen 5 5600X', 'AMD', '3.7GHz', 1),
('CPU Núcleo 6', 'Ryzen 5 5600X', 'AMD', '3.7GHz', 1),
('CPU Núcleo 7', 'Ryzen 5 5600X', 'AMD', '3.7GHz', 1),
('CPU Núcleo 8', 'Ryzen 5 5600X', 'AMD', '3.7GHz', 1);

INSERT IGNORE INTO metricaRede (fkEmpresa, nome, medida, min, max) VALUES
(1, 'Velocidade de Download', 'Mbps', 0, 1000),
(1, 'Velocidade de Upload', 'Mbps', 0, 1000),
(1, 'Latência', 'ms', 0, 300);

INSERT IGNORE INTO componenteRede (nome, interfaceRede, fkMetricaRede) VALUES
('Adaptador Ethernet Intel', 'eth0', 1),
('Placa de Rede Wi-Fi TP-Link', 'wlan0', 2),
('Interface Virtual VPN', 'tun0', 3);

INSERT IGNORE INTO incidente (fkFuncionario, fkEmpresa, titulo, descricao) VALUES
(1, 1, 'Falha no servidor', 'Servidor principal não responde'),
(2, 2, 'Rede lenta', 'Usuários reportam lentidão'),
(3, 3, 'Erro de login', 'Falha no acesso ao sistema');
""";

        try {
            for (String comando : scriptDML.split(";")) {
                if (!comando.trim().isEmpty()) {
                    template.execute(comando.trim());
                }
            }
            System.out.println(">>> Dados iniciais inseridos com sucesso (usando INSERT IGNORE para evitar duplicatas).");
        } catch (DataAccessException e) {
            System.err.println(">>> Erro ao inserir dados iniciais. Verifique as FKs e a sintaxe.");
            e.printStackTrace();
        }

        System.out.println("""
                 __    __     ______     __   __     __     ______   ______     ______     ______     __    __     ______     __   __     ______   ______
                /\\ "-./  \\   /\\  __ \\   /\\ "-.\\ \\   /\\ \\   /\\__  _\\ /\\  __ \\   /\\  == \\   /\\  __ \\   /\\ "-./  \\   /\\  ___\\   /\\ "-.\\ \\   /\\__  _\\ /\\  __ \\
                \\ \\ \\-./\\ \\  \\ \\ \\/\\ \\  \\ \\ \\-.  \\  \\ \\ \\  \\/_/\\ \\/ \\ \\ \\/\\ \\  \\ \\  __<   \\ \\  __ \\  \\ \\ \\-./\\ \\  \\ \\  __\\   \\ \\ \\-.  \\  \\/_/\\ \\/ \\ \\ \\/\\ \\
                 \\ \\_\\ \\ \\_\\  \\ \\_____\\  \\ \\_\\\\"\\_\\  \\ \\_\\    \\ \\_\\  \\ \\_____\\  \\ \\_\\ \\_\\  \\ \\_\\ \\_\\  \\ \\_\\ \\ \\_\\  \\ \\_____\\  \\ \\_\\\\"\\_\\    \\ \\_\\  \\ \\_____\\
                  \\/_/  \\/_/   \\/_____/   \\/_/ \\/_/   \\/_/     \\/_/   \\/_____/   \\/_/ /_/   \\/_/\\/_/   \\/_/  \\/_/   \\/_____/   \\/_/ \\/_/     \\/_/   \\/_____/

                 __  __     ______     ______     _____     __   __   __     ______     __     ______     __   __
                /\\ \\_\\ \\   /\\  __ \\   /\\  == \\   /\\  __-.  /\\ \\ / /  /\\ \\   /\\  ___\\   /\\ \\   /\\  __ \\   /\\ "-.\\ \\
                \\ \\  __ \\  \\ \\  __ \\  \\ \\  __<   \\ \\ \\/\\ \\ \\ \\ \\'/   \\ \\ \\  \\ \\___  \\  \\ \\ \\  \\ \\ \\/\\ \\  \\ \\ \\-.  \\
                 \\ \\_\\ \\_\\  \\ \\_\\ \\_\\  \\ \\_\\ \\_\\  \\ \\____-  \\ \\__|    \\ \\_\\  \\/\\_____\\  \\ \\_\\  \\ \\_____\\  \\ \\_\\\\"\\_\\
                  \\/_/\\/_/   \\/_/\\/_/   \\/_/ /_/   \\/____/   \\/_/      \\/_/   \\/_____/   \\/_/   \\/_____/   \\/_/ \\/_/

                ╔════════════════════════════════════════════════════════════════╗
                ║                      MONITORAMENTO DE REDE                     ║
                ╚════════════════════════════════════════════════════════════════╝
                """);

        String macAddressMaquinaAtual = rede.getGrupoDeInterfaces().getInterfaces().stream()
                .filter(i -> !i.getEnderecoMac().startsWith("00:00:00") && !i.getEnderecoMac().isEmpty())
                .findFirst()
                .map(RedeInterface::getEnderecoMac)
                .orElse("00:00:00:00:00:00");

        Integer FK_EMPRESA_FIXO = 1;
        Integer FK_SISTEMA_FIXO = 1;

        Integer fkMaquina = buscarOuCadastrarId(
                template,
                "SELECT idMaquina FROM maquina WHERE macAddress = ?",
                "INSERT INTO maquina (fkEmpresa, fkSistema, macAddress, localizacao) VALUES (?, ?, ?, 'Desconhecida')",
                macAddressMaquinaAtual,
                FK_EMPRESA_FIXO, FK_SISTEMA_FIXO, macAddressMaquinaAtual
        );

        tempoAnteriorMs = System.currentTimeMillis();


        while (true) {
            leituraAtual++;

            try {
                for (RedeInterface redeInterface : interfaces) {
                    if (!redeInterface.getEnderecoIpv4().isEmpty() && redeInterface.getBytesRecebidos() > 0) {

                        long bytesRecebidosAtual = redeInterface.getBytesRecebidos();
                        long bytesEnviadosAtual = redeInterface.getBytesEnviados();
                        long tempoAtualMs = System.currentTimeMillis();

                        double diferencaTempoSegundos = (tempoAtualMs - tempoAnteriorMs) / 1000.0;

                        if (diferencaTempoSegundos == 0.0) {
                            diferencaTempoSegundos = 5.0;
                        }

                        long bytesRecebidosDiff = bytesRecebidosAtual - bytesRecebidosAnterior;
                        long bytesEnviadosDiff = bytesEnviadosAtual - bytesEnviadosAnterior;

                        Double throughputTotalBytesPorSegundo = (double) (bytesRecebidosDiff + bytesEnviadosDiff) / diferencaTempoSegundos;

                        Double throughputMbps = (throughputTotalBytesPorSegundo / 1024.0 / 1024.0) * 8.0;


                        if (throughputMbps < 0 || bytesRecebidosAnterior == 0) {
                            throughputMbps = 0.0;
                        }

                        bytesRecebidosAnterior = bytesRecebidosAtual;
                        bytesEnviadosAnterior = bytesEnviadosAtual;
                        tempoAnteriorMs = tempoAtualMs;

                        int largura = 62;
                        String nomeInterface = redeInterface.getNome();
                        String ipv4 = redeInterface.getEnderecoIpv4().isEmpty() ? "" : redeInterface.getEnderecoIpv4().get(0);

                        Integer fkMetricaRede;
                        Integer fkAlertaRede;

                        if (bytesRecebidosDiff > bytesEnviadosDiff * 1.5) {
                            fkMetricaRede = METRICA_DOWNLOAD_ID;
                            fkAlertaRede = ALERTA_DOWNLOAD_ID;
                        } else if (bytesEnviadosDiff > bytesRecebidosDiff * 1.5) {
                            fkMetricaRede = METRICA_UPLOAD_ID;
                            fkAlertaRede = ALERTA_UPLOAD_ID;
                        } else {
                            fkMetricaRede = METRICA_LATENCIA_ID;
                            fkAlertaRede = ALERTA_LATENCIA_ID;
                        }

                        Integer fkComponenteRede = buscarOuCadastrarId(
                                template,
                                "SELECT idComponenteRede FROM componenteRede WHERE nome = ?",
                                "INSERT INTO componenteRede (nome, interfaceRede, fkMetricaRede) VALUES (?, ?, ?)",
                                nomeInterface,
                                nomeInterface, nomeInterface, METRICA_THROUGHPUT_ID
                        );

                        Double metricaMin = buscarMetrica(template,
                                "SELECT min FROM metricaRede join componenteRede on fkMetricaRede " +
                                        " = idMetricaRede WHERE idComponenteRede = ?", fkComponenteRede);

                        if(throughputMbps < metricaMin){
                            template.update("""
                                    insert into alertaRede (fkMetricaRede, estado) values(?, 'Crítico')""",
                                    METRICA_THROUGHPUT_ID);

                            List<Integer> id = template.query(
                                    """
                                            SELECT idAlertaRede FROM alertaRede ORDER BY idAlertaRede DESC LIMIT 1; """,
                                    (rs, rowNum) -> rs.getInt(1)
                            );

                            fkAlertaRede = id.get(0);
                        }

                        System.out.println(metricaMin);

                        if (fkMaquina != null && fkComponenteRede != null && fkMetricaRede != null) {
                            String descricao = "Rede saudável";

                            if(fkAlertaRede != null){
                                descricao = "Sua rede está com a velocidade lenta. Verifique-a!";
                            }

                            Double mbEnviados = (double) bytesEnviadosAtual / 1024.0 / 1024.0;
                            Double mbRecebidos = (double) bytesRecebidosAtual / 1024.0 / 1024.0;
                            Double pacotesEnviados = (double) redeInterface.getPacotesEnviados();
                            Double pacotesRecebidos = (double) redeInterface.getPacotesRecebidos();

                            template.update("""
                            INSERT INTO logMonitoramentoRede (
                            fkMaquina, fkComponenteRede, fkMetricaRede, fkAlertaRede,
                            ipv4, velocidadeMbps, mbEnviados, mbRecebidos, pacotesEnviados, pacotesRecebidos, descricao
                            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
                                 """,
                                    fkMaquina,
                                    fkComponenteRede,
                                    fkMetricaRede,
                                    fkAlertaRede,
                                    ipv4,
                                    throughputMbps,
                                    mbEnviados,
                                    mbRecebidos,
                                    pacotesEnviados,
                                    pacotesRecebidos,
                                    descricao
                            );


                            String linha0 = formatarLinha("Leitura " + leituraAtual, largura);
                            String linha1 = formatarLinha("Interface: " + nomeInterface, largura);
                            String linha2 = formatarLinha("IPv4: " + ipv4, largura);
                            String linha3 = formatarLinha("Bytes recebidos: " + Conversor.formatarBytes(redeInterface.getBytesRecebidos()), largura);
                            String linha4 = formatarLinha("Bytes enviados: " + Conversor.formatarBytes(redeInterface.getBytesEnviados()), largura);
                            String linha5 = formatarLinha("Pacotes recebidos: " + Conversor.formatarBytes(redeInterface.getPacotesRecebidos()), largura);
                            String linha6 = formatarLinha("Pacotes enviados: " + Conversor.formatarBytes(redeInterface.getPacotesEnviados()), largura);
                            String linha7 = formatarLinha("Throughput (Mbps): " + String.format("%.2f", throughputMbps), largura);

                            System.out.println("""
                                ╔════════════════════════════════════════════════════════════════╗
                                ║ %s ║
                                ║ %s ║
                                ║ %s ║
                                ║ %s ║
                                ║ %s ║
                                ║ %s ║
                                ║ %s ║
                                ║ %s ║
                                ╚════════════════════════════════════════════════════════════════╝
                                """.formatted(linha0, linha1, linha2, linha3, linha4, linha5, linha6, linha7));

                        } else {
                            System.err.println(">>> ERRO: Uma FK crucial é nula (FKs Metricas/Alertas, Máquina ou Componente). Verificar se as tabelas estão populadas.");
                        }
                    }
                }
                Thread.sleep(2000);
            } catch (EmptyResultDataAccessException e) {
                System.err.println("\n\n--- ERRO CRÍTICO: METRICA OU ALERTA NÃO ENCONTRADO NA ITERAÇÃO " + leituraAtual + " ---");
                System.err.println("Causa: Este erro foi corrigido com a alteração das constantes. Se ainda ocorrer, verifique a tabela 'maquina' ou 'componenteRede'.");
                System.err.println("Mensagem: " + e.getMessage());
                try { Thread.sleep(5000); } catch (InterruptedException ignored) {}
            } catch (DataAccessException e) {
                System.err.println("\n--- ERRO CRÍTICO DE BANCO DE DADOS NA ITERAÇÃO " + leituraAtual + " ---");
                System.err.println("Mensagem: " + e.getMessage());
                e.printStackTrace();
                try { Thread.sleep(5000); } catch (InterruptedException ignored) {}
            } catch (InterruptedException e) {
                System.err.println("A thread de monitoramento foi interrompida.");
                Thread.currentThread().interrupt();
            }
        }
    }

    private static String formatarLinha(String texto, int largura) {
        if (texto.length() > largura) {
            return texto.substring(0, largura - 1);
        } else {
            return String.format("%-" + largura + "s", texto);
        }
    }
}