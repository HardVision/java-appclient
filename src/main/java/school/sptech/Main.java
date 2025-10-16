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

        template.execute("DROP TABLE IF EXISTS rede");

        template.execute("""
                CREATE TABLE rede (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    interface_nome VARCHAR(50) NOT NULL,
                    ipv4 VARCHAR(15),
                    bytes_recebidos DECIMAL(15, 2),
                    bytes_enviados DECIMAL(15, 2),
                    pacotes_recebidos DECIMAL(15, 2),
                    pacotes_enviados DECIMAL(15, 2),
                    data_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP
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

                        String ipv4 = redeInterface.getEnderecoIpv4().isEmpty() ? "" : redeInterface.getEnderecoIpv4().get(0);

                        template.update("""
                                        INSERT INTO rede (
                                            interface_nome, ipv4, bytes_recebidos, bytes_enviados, pacotes_recebidos, pacotes_enviados
                                        ) VALUES (?, ?, ?, ?, ?, ?);""",
                                redeInterface.getNome(),
                                ipv4,
                                redeInterface.getBytesRecebidos(),
                                redeInterface.getBytesEnviados(),
                                redeInterface.getPacotesRecebidos(),
                                redeInterface.getPacotesEnviados()
                        );
                    }
                }
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        List<school.sptech.Rede> leituras_interfaces = template
                .query("SELECT * FROM rede", new BeanPropertyRowMapper<>(school.sptech.Rede.class));
    }

    private static String formatarLinha(String texto, int largura) {
        if (texto.length() > largura) {
            return texto.substring(0, largura - 1);
        } else {
            return String.format("%-" + largura + "s", texto);
        }
    }
}