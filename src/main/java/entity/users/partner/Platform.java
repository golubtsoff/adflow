package entity.users.partner;

import entity.users.PictureFormat;
import entity.users.Action;
import entity.users.Status;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "PLATFORMS")
public class Platform {

    public static final String ID = "id";
    public static final String PARTNER_ID = "partner_id";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String CPM_RATE = "cpm_rate";
    public static final String PICTURE_FORMAT_ID = "picture_format_id";
    public static final String CREATION_DATE = "creation_date";
    public static final String REMOVED_DATE = "removed_date";
    public static final String ACTION = "action";
    public static final String STATUS = "status";

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

    @Column(name = CREATION_DATE)
    private LocalDateTime creationDate;

    @Column(name = REMOVED_DATE)
    private LocalDateTime removedDate;

    @Column(name = ACTION)
    @Enumerated(EnumType.STRING)
    private Action action;

    @Column(name = STATUS)
    @Enumerated(EnumType.STRING)
    private Status status;

    public Platform() {
    }

    public Platform(Partner partner) {
        this(null, partner, null, null, null, null);
    }

    public Platform(
            Partner partner,
            String title,
            String description,
            BigDecimal cpmRate,
            PictureFormat pictureFormat) {
        this(null, partner, title, description, cpmRate, pictureFormat);
    }

    public Platform(
            Long id,
            Partner partner,
            String title,
            String description,
            BigDecimal cpmRate,
            PictureFormat pictureFormat) {
        this(null, partner, title, description, cpmRate, pictureFormat, Action.STOP, Status.CHECKING);
    }

    public Platform(
            Partner partner,
            String title,
            String description,
            BigDecimal cpmRate,
            PictureFormat pictureFormat,
            Action action,
            Status status) {
        this(null, partner, title, description, cpmRate, pictureFormat, action, status);
    }

    public Platform(
            Long id,
            Partner partner,
            String title,
            String description,
            BigDecimal cpmRate,
            PictureFormat pictureFormat,
            Action action,
            Status status) {
        this.id = id;
        this.partner = partner;
        this.title = title;
        this.description = description;
        this.cpmRate = cpmRate;
        this.pictureFormat = pictureFormat;
        this.creationDate = LocalDateTime.now();
        this.status = status;
        this.action = action;
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
        if (status == Status.REMOVED){
            this.setRemovedDate(LocalDateTime.now());
            this.setAction(Action.STOP);
        }
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getRemovedDate() {
        return removedDate;
    }

    public void setRemovedDate(LocalDateTime removedDate) {
        this.removedDate = removedDate;
    }

    @Override
    public String toString() {
        return "Platform{" +
                "id=" + id +
                ", partner=" + partner.getId() +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", cpmRate=" + cpmRate +
                ", pictureFormat=" + pictureFormat +
                ", creationDate=" + creationDate +
                ", removedDate=" + removedDate +
                ", action=" + action +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Platform)) return false;
        Platform platform = (Platform) o;
        return Objects.equals(getId(), platform.getId()) &&
                Objects.equals(getPartner(), platform.getPartner()) &&
                Objects.equals(getTitle(), platform.getTitle()) &&
                Objects.equals(getDescription(), platform.getDescription()) &&
                Objects.equals(getCpmRate(), platform.getCpmRate()) &&
                Objects.equals(getPictureFormat(), platform.getPictureFormat()) &&
                Objects.equals(getCreationDate(), platform.getCreationDate()) &&
                Objects.equals(getRemovedDate(), platform.getRemovedDate()) &&
                getAction() == platform.getAction() &&
                getStatus() == platform.getStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getId(),
                getPartner(),
                getTitle(),
                getDescription(),
                getCpmRate(),
                getPictureFormat(),
                getCreationDate(),
                getRemovedDate(),
                getAction(),
                getStatus());
    }
}
