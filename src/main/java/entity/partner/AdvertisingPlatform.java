package entity.partner;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(name = "PARTNER_ID")
    private Partner partner;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "CPM_RATE")
    private BigDecimal cpmRate;

    public AdvertisingPlatform() {
    }

    public AdvertisingPlatform(Partner partner, String title, String description, BigDecimal cpmRate) {
        this(null, partner, title, description, cpmRate);
    }

    public AdvertisingPlatform(Long id, Partner partner, String title, String description, BigDecimal cpmRate) {
        this.id = id;
        this.partner = partner;
        this.title = title;
        this.description = description;
        this.cpmRate = cpmRate;
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

    public Partner getPartner() {
        return partner;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
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
                Objects.equals(partner, that.partner);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, partner, title, description, cpmRate);
    }

    @Override
    public String toString() {
        return "AdvertisingPlatform{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", cpmRate=" + cpmRate +
                ", partner='" + partner + '\'' +
                '}';
    }
}
