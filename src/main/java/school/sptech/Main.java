package school.sptech;

import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.rede.Rede;
import com.github.britooo.looca.api.group.rede.RedeInterface;
import com.github.britooo.looca.api.util.Conversor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        Looca looca = new Looca();
        Rede rede = looca.getRede();

        List<RedeInterface> interfaces = rede.getGrupoDeInterfaces().getInterfaces();

        Conexao conexao = new Conexao();
        JdbcTemplate template = new JdbcTemplate(conexao.getConexao());

        template.execute("""
                        CREATE TABLE IF NOT EXISTS logMonitoramentoRede (
                            idMonitoramentoRede INT AUTO_INCREMENT,
                            fkAlertaRede INT,
                            fkComponenteRede INT,
                            fkMaquina INT,
                            fkMetricaRede INT,
                            ipv4 char(15) NOT NULL,
                            velocidadeMbps decimal(10,2) NOT NULL,
                            mbEnviados decimal(10,2) NOT NULL,
                            mbRecebidos decimal(10,2) NOT NULL,
                            pacotesEnviados decimal(10,2) NOT NULL,
                            pacotesRecebidos decimal(10,2) NOT NULL,
                            dtHora TIMESTAMP DEFAULT CURRENT_TIMESTAMP(),
                            CONSTRAINT pkLogMonitoramentoRede PRIMARY KEY (idMonitoramentoRede, fkMaquina, fkComponenteRede, fkAlertaRede),
                            FOREIGN KEY (fkMaquina) REFERENCES maquina(idMaquina),
                            FOREIGN KEY (fkComponenteRede) REFERENCES componenteRede(idComponenteRede),
                            FOREIGN KEY (fkMetricaRede) REFERENCES metricaRede(idMetricaRede),
                            FOREIGN KEY (fkAlertaRede) REFERENCES alertaRede(idAlertaRede)
                        );
                """);

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

        for (int i = 0; i < 10; i++) {
            try {
                for (RedeInterface redeInterface : interfaces) {
                    if (!redeInterface.getEnderecoIpv4().isEmpty() && redeInterface.getBytesRecebidos() > 0) {

                        int largura = 62;

                        String linha0 = formatarLinha("Leitura " + (i + 1), largura);
                        String linha1 = formatarLinha("Interface: " + redeInterface.getNome(), largura);
                        String linha2 = formatarLinha("IPv4: " + redeInterface.getEnderecoIpv4(), largura);
                        String linha3 = formatarLinha("Bytes recebidos: " + Conversor.formatarBytes(redeInterface.getBytesRecebidos()), largura);
                        String linha4 = formatarLinha("Bytes enviados: " + Conversor.formatarBytes(redeInterface.getBytesEnviados()), largura);
                        String linha5 = formatarLinha("Pacotes recebidos: " + Conversor.formatarBytes(redeInterface.getPacotesRecebidos()), largura);
                        String linha6 = formatarLinha("Pacotes enviados: " + Conversor.formatarBytes(redeInterface.getPacotesEnviados()), largura);

                        System.out.println("""
                        ╔════════════════════════════════════════════════════════════════╗
                        ║ %s ║
                        ║ %s ║
                        ║ %s ║
                        ║ %s ║
                        ║ %s ║
                        ║ %s ║
                        ║ %s ║
                        ╚════════════════════════════════════════════════════════════════╝
                        """.formatted(linha0, linha1, linha2, linha3, linha4, linha5, linha6));

                        // Pegando o MAC e nome da interface
                        String macAddress = redeInterface.getEnderecoMac();
                        String nomeInterface = redeInterface.getNome();

                        // fkMaquina: busca pela máquina com o MAC correspondente
                        Integer fkMaquina = template.queryForObject(
                                "SELECT idMaquina FROM maquina WHERE macAddress = ?",
                                Integer.class,
                                macAddress
                        );

                        // fkComponenteRede: busca pelo componente de rede com o nome da interface
                        Integer fkComponenteRede = template.queryForObject(
                                "SELECT idComponenteRede FROM componenteRede WHERE nome = ?",
                                Integer.class,
                                nomeInterface
                        );

                        // Definir a métrica de rede dinamicamente
                        String nomeMetrica;
                        long bytesEnviados = redeInterface.getBytesEnviados();
                        long bytesRecebidos = redeInterface.getBytesRecebidos();
                        long pacotesTotal = redeInterface.getPacotesEnviados() + redeInterface.getPacotesRecebidos();

                        if (pacotesTotal < 10) {
                            nomeMetrica = "Latência";
                        } else if (bytesRecebidos >= bytesEnviados) {
                            nomeMetrica = "Velocidade de Download";
                        } else {
                            nomeMetrica = "Velocidade de Upload";
                        }

                        Integer fkMetricaRede = template.queryForObject(
                                "SELECT idMetricaRede FROM metricaRede WHERE nome = ?",
                                Integer.class,
                                nomeMetrica
                        );

                        // fkAlertaRede baseado na métrica
                        Integer fkAlertaRede = template.queryForObject(
                                "SELECT idAlertaRede FROM alertaRede WHERE fkMetricaRede = ?",
                                Integer.class,
                                fkMetricaRede
                        );

                        Double mbEnviados = bytesEnviados / 1024.0 / 1024.0;
                        Double mbRecebidos = bytesRecebidos / 1024.0 / 1024.0;
                        Long pacotesEnviados = redeInterface.getPacotesEnviados();
                        Long pacotesRecebidos = redeInterface.getPacotesRecebidos();
                        String ipv4 = redeInterface.getEnderecoIpv4().isEmpty() ? "" : redeInterface.getEnderecoIpv4().get(0);

                        template.update("""
                                INSERT INTO logMonitoramentoRede (
                                    fkMaquina, fkComponenteRede, fkMetricaRede, fkAlertaRede,
                                    ipv4, mbEnviados, mbRecebidos, pacotesEnviados, pacotesRecebidos
                                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);
                                """,
                                fkMaquina,
                                fkComponenteRede,
                                fkMetricaRede,
                                fkAlertaRede,
                                ipv4,
                                mbEnviados,
                                mbRecebidos,
                                pacotesEnviados,
                                pacotesRecebidos
                        );
                    }
                }
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        List<school.sptech.Rede> leituras_interfaces = template
                .query("SELECT * FROM logMonitoramentoRede", new BeanPropertyRowMapper<>(school.sptech.Rede.class));
    }

    private static String formatarLinha(String texto, int largura) {
        if (texto.length() > largura) {
            return texto.substring(0, largura - 1);
        } else {
            return String.format("%-" + largura + "s", texto);
        }
    }
}