package school.sptech;

import java.time.LocalDateTime;

public class Rede  {

    private Integer id;
    private String interface_nome;
    private String ipv4;
    private Double bytes_recebidos;
    private Double bytes_enviados;
    private Double pacotes_recebidos;
    private Double pacotes_enviados;
    private LocalDateTime data_hora;

    public Rede() {
    }

    public Rede(Integer id, String interface_nome, String ipv4, Double bytes_recebidos, Double bytes_enviados, Double pacotes_recebidos, Double pacotes_enviados, LocalDateTime data_hora) {
        this.id = id;
        this.interface_nome = interface_nome;
        this.ipv4 = ipv4;
        this.bytes_recebidos = bytes_recebidos;
        this.bytes_enviados = bytes_enviados;
        this.pacotes_recebidos = pacotes_recebidos;
        this.pacotes_enviados = pacotes_enviados;
        this.data_hora = data_hora;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getInterface_nome() {
        return interface_nome;
    }

    public void setInterface_nome(String interface_nome) {
        this.interface_nome = interface_nome;
    }

    public String getIpv4() {
        return ipv4;
    }

    public void setIpv4(String ipv4) {
        this.ipv4 = ipv4;
    }

    public Double getBytes_recebidos() {
        return bytes_recebidos;
    }

    public void setBytes_recebidos(Double bytes_recebidos) {
        this.bytes_recebidos = bytes_recebidos;
    }

    public Double getBytes_enviados() {
        return bytes_enviados;
    }

    public void setBytes_enviados(Double bytes_enviados) {
        this.bytes_enviados = bytes_enviados;
    }

    public Double getPacotes_recebidos() {
        return pacotes_recebidos;
    }

    public void setPacotes_recebidos(Double pacotes_recebidos) {
        this.pacotes_recebidos = pacotes_recebidos;
    }

    public Double getPacotes_enviados() {
        return pacotes_enviados;
    }

    public void setPacotes_enviados(Double pacotes_enviados) {
        this.pacotes_enviados = pacotes_enviados;
    }

    public LocalDateTime getData_hora() {
        return data_hora;
    }

    public void setData_hora(LocalDateTime data_hora) {
        this.data_hora = data_hora;
    }

    @Override
    public String toString() {
        return "Rede{" +
                "id=" + id +
                ", interface_nome='" + interface_nome + '\'' +
                ", ipv4='" + ipv4 + '\'' +
                ", bytes_recebidos=" + bytes_recebidos +
                ", bytes_enviados=" + bytes_enviados +
                ", pacotes_recebidos=" + pacotes_recebidos +
                ", pacotes_enviados=" + pacotes_enviados +
                ", data_hora=" + data_hora +
                '}';
    }
}