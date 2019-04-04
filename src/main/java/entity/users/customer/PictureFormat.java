package entity.users.customer;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "PICTURE_FORMATS")
public class PictureFormat {

    public static final String ID = "ID";
    public static final String WIDTH = "WIDTH";
    public static final String HEIGHT = "HEIGHT";

    @Id
    @Column(name = ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = WIDTH)
    private int width;

    @Column(name = HEIGHT)
    private int height;


    public PictureFormat() {}

    public PictureFormat(int width, int height) {
        this(null, width, height);
    }

    public PictureFormat(Long id, int width, int height) {
        this.id = id;
        this.width = width;
        this.height = height;
    }

    public Long getId() {
        return id;
    }


    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "PictureFormat{" +
                "id=" + id +
                ", width=" + width +
                ", height=" + height +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PictureFormat)) return false;
        PictureFormat that = (PictureFormat) o;
        return getWidth() == that.getWidth() &&
                getHeight() == that.getHeight();
    }

    @Override
    public int hashCode() {

        return Objects.hash(getWidth(), getHeight());
    }
}
