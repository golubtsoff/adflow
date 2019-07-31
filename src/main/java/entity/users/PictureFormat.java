package entity.users;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(
        name = "PICTURE_FORMATS",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"WIDTH", "HEIGHT"})}
)
public class PictureFormat {

    public static final String ID = "ID";
    public static final String WIDTH = "WIDTH";
    public static final String HEIGHT = "HEIGHT";
    public static final String CAN_BE_USED = "CAN_BE_USED";

    @Id
    @Column(name = ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = WIDTH, nullable=false)
    private int width;

    @Column(name = HEIGHT, nullable=false)
    private int height;

    @Column(name = CAN_BE_USED)
    private boolean canBeUsed;

    public PictureFormat() {}

    public PictureFormat(int width, int height) {
        this(null, width, height);
    }

    public PictureFormat(Long id, int width, int height) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.canBeUsed = true;
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

    public boolean isCanBeUsed() {
        return canBeUsed;
    }

    public void setCanBeUsed(boolean canBeUsed) {
        this.canBeUsed = canBeUsed;
    }

    @Override
    public String toString() {
        return "PictureFormat{" +
                "id=" + id +
                ", width=" + width +
                ", height=" + height +
                ", canBeUsed=" + canBeUsed +
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
