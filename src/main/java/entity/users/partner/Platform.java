package entity.users.partner;

import entity.users.customer.PictureFormat;
import entity.users.Action;
import entity.users.Status;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "ADVERTISING_PLATFORMS")
public class Platform {

    public static final String ID = "ID";
    public static final String PARTNER_ID = "PARTNER_ID";
    public static final String TITLE = "TITLE";
    public static final String DESCRIPTION = "DESCRIPTION";
    public static final String CPM_RATE = "CPM_RATE";
    public static final String PICTURE_FORMAT_ID = "PICTURE_FORMAT_ID";
    public static final String ACTION = "ACTION";
    public static final String STATUS = "STATUS";

    @Id
    @Column(name = ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(name = PARTNER_ID)
    private Partner partner;

    @Column(name = TITLE)
    private String title;

    @Column(name = DESCRIPTION)
    private String description;

    @Column(name = CPM_RATE)
    private BigDecimal cpmRate;

    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(name = PICTURE_FORMAT_ID)
    private PictureFormat pictureFormat;

    @Column(name = ACTION)
    @Enumerated(EnumType.STRING)
    private Action action;

    @Column(name = STATUS)
    @Enumerated(EnumType.STRING)
    private Status status;

    public Platform() {
    }

    public Platform(Partner partner, String title, String description, BigDecimal cpmRate) {
        this(null, partner, title, description, cpmRate);
    }

    public Platform(Long id, Partner partner, String title, String description, BigDecimal cpmRate) {
        this.id = id;
        this.partner = partner;
        this.title = title;
        this.description = description;
        this.cpmRate = cpmRate;
        this.status = Status.CHECKING;
        this.action = Action.STOP;
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

    public PictureFormat getPictureFormat() {
        return pictureFormat;
    }

    public void setPictureFormat(PictureFormat pictureFormat) {
        this.pictureFormat = pictureFormat;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Platform{" +
                "id=" + id +
                ", partner=" + partner +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", cpmRate=" + cpmRate +
                ", pictureFormat=" + pictureFormat +
                ", action=" + action +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Platform)) return false;
        Platform that = (Platform) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getTitle(), that.getTitle());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId(), getTitle());
    }
}
