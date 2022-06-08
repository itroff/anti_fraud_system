package antifraud.models;

import javax.persistence.*;
import java.util.Map;

@Entity(name = "ip")
@Table(name = "ip")
public class IP implements CrudInterface {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public IP(String ip) {
        this.ip = ip;
    }

    public IP() {

    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Column
    private String ip;

    public boolean validate() {
        return validateIP(this.ip);
    }

    public String value() {
        return this.ip;
    }

    public static boolean validateIP(String ip) {
        if (ip.matches("\\b((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(\\.|$)){4}\\b")) {
            return true;
        }
        return false;
    }

    public Map<String, String> removeMsg() {
        return Map.of("status", "IP " + this.ip + " successfully removed!");
    }

}
