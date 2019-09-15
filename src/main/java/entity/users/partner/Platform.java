package entity.users.partner;

import entity.users.AbstractCampaignPlatform;
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
public class Platform extends AbstractCampaignPlatform {

    public static final String PARTNER_ID = "partner_id";
    public static final String PICTURE_FORMAT_ID = "picture_format_id";

    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(name = PARTNER_ID)
    private Partner partner;

    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(name = PICTURE_FORMAT_ID)
    private PictureFormat pictureFormat;

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
