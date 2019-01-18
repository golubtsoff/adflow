package entity.session;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "VIEWERS")
public class Viewer {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "IP")
    private String ip;

    public Viewer() {
    }

    public Viewer(String name, String ip) {
        this(null, name, ip);
    }

    public Viewer(Long id, String name, String ip) {
        this.id = id;
        this.name = name;
        this.ip = ip;
    }

    public Long getId() {
        return id;
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
                "id=" + id +
                ", name='" + name + '\'' +
                ", ip='" + ip + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Viewer viewer = (Viewer) o;
        return Objects.equals(id, viewer.id) &&
                Objects.equals(name, viewer.name) &&
                Objects.equals(ip, viewer.ip);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, ip);
    }
}
