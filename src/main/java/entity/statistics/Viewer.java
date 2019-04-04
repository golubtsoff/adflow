package entity.statistics;

import javax.persistence.*;
import java.util.Objects;

@Embeddable
public class Viewer {

    public static final String NAME = "NAME";
    public static final String IP = "IP";

    @Column(name = NAME)
    private String name;

    @Column(name = IP)
    private String ip;

    public Viewer() {
    }

    public Viewer(String name, String ip) {
        this.name = name;
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "Viewer{" +
                ", name='" + name + '\'' +
                ", ip='" + ip + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Viewer viewer = (Viewer) o;
        return Objects.equals(name, viewer.name) &&
                Objects.equals(ip, viewer.ip);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, ip);
    }
}
