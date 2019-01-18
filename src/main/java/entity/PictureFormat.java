package entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "PICTURE_FORMATS")
public class PictureFormat {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "FORMAT")
    private String format;


    public PictureFormat() {}

    public PictureFormat(String format) {
        this(null, format);
    }

    public PictureFormat(Long id, String format) {
        this.id = id;
        this.format = format;
    }

    public Long getId() {
        return id;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public String toString() {
        return "PictureFormat{" +
                "id=" + id +
                ", format='" + format + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PictureFormat that = (PictureFormat) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(format, that.format);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, format);
    }
}
