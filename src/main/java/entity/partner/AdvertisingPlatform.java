package entity.partner;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "ADVERTISING_PLATFORMS")
public class AdvertisingPlatform {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "CPM_RATE")
    private BigDecimal cpmRate;

    @Column(name = "KEY")
    private String key;


    public AdvertisingPlatform() {
    }

    public AdvertisingPlatform(String title, String description, BigDecimal cpmRate, String key) {
        this(null, title, description, cpmRate, key);
    }

    public AdvertisingPlatform(Long id, String title, String description, BigDecimal cpmRate, String key) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.cpmRate = cpmRate;
        this.key = key;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getCpmRate() {
        return cpmRate;
    }

    public void setCpmRate(BigDecimal cpmRate) {
        this.cpmRate = cpmRate;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdvertisingPlatform that = (AdvertisingPlatform) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(title, that.title) &&
                Objects.equals(description, that.description) &&
                Objects.equals(cpmRate, that.cpmRate) &&
                Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, title, description, cpmRate, key);
    }

    @Override
    public String toString() {
        return "AdvertisingPlatform{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", cpmRate=" + cpmRate +
                ", key='" + key + '\'' +
                '}';
    }
}
