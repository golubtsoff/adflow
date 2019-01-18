package entity.session;

import entity.PictureFormat;
import entity.partner.AdvertisingPlatform;
import entity.partner.Partner;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "SESSIONS")
public class Session {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(name = "PARTNER_ID")
    private Partner partner;

    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(name = "PICTURE_FORMAT_ID")
    private PictureFormat pictureFormat;

    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(name = "ADVERTISING_PLATFORM_ID")
    private AdvertisingPlatform advertisingPlatform;

    @Column(name = "DISPLAYS_COUNTER")
    private int displaysCounter;

    @Column(name = "CLICK_COUNTER")
    private int clickCounter;

    @OneToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(name = "VIEWER_ID")
    private Viewer viewer;

    @Column(name = "CREATION_TIME")
    private LocalDateTime creationTime;

    @Column(name = "CLOSING_TIME")
    private LocalDateTime closingTime;

    public Session() {
    }

    public Session(
            Partner partner,
            PictureFormat pictureFormat,
            AdvertisingPlatform advertisingPlatform,
            int displaysCounter,
            int clickCounter,
            Viewer viewer,
            LocalDateTime creationTime,
            LocalDateTime closingTime) {
        this(null, partner, pictureFormat, advertisingPlatform, displaysCounter,
                clickCounter, viewer, creationTime, closingTime);
    }

    public Session(
            Long id,
            Partner partner,
            PictureFormat pictureFormat,
            AdvertisingPlatform advertisingPlatform,
            int displaysCounter,
            int clickCounter,
            Viewer viewer,
            LocalDateTime creationTime,
            LocalDateTime closingTime) {
        this.id = id;
        this.partner = partner;
        this.pictureFormat = pictureFormat;
        this.advertisingPlatform = advertisingPlatform;
        this.displaysCounter = displaysCounter;
        this.clickCounter = clickCounter;
        this.viewer = viewer;
        this.creationTime = creationTime;
        this.closingTime = closingTime;
    }

    public Long getId() {
        return id;
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

    public AdvertisingPlatform getAdvertisingPlatform() {
        return advertisingPlatform;
    }

    public void setAdvertisingPlatform(AdvertisingPlatform advertisingPlatform) {
        this.advertisingPlatform = advertisingPlatform;
    }

    public int getDisplaysCounter() {
        return displaysCounter;
    }

    public void setDisplaysCounter(int displaysCounter) {
        this.displaysCounter = displaysCounter;
    }

    public int getClickCounter() {
        return clickCounter;
    }

    public void setClickCounter(int clickCounter) {
        this.clickCounter = clickCounter;
    }

    public Viewer getViewer() {
        return viewer;
    }

    public void setViewer(Viewer viewer) {
        this.viewer = viewer;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public LocalDateTime getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(LocalDateTime closingTime) {
        this.closingTime = closingTime;
    }

    @Override
    public String toString() {
        return "Session{" +
                "id=" + id +
                ", partner=" + partner +
                ", pictureFormat=" + pictureFormat +
                ", advertisingPlatform=" + advertisingPlatform +
                ", displaysCounter=" + displaysCounter +
                ", clickCounter=" + clickCounter +
                ", viewer=" + viewer +
                ", creationTime=" + creationTime +
                ", closingTime=" + closingTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Session session = (Session) o;
        return displaysCounter == session.displaysCounter &&
                clickCounter == session.clickCounter &&
                Objects.equals(id, session.id) &&
                Objects.equals(partner, session.partner) &&
                Objects.equals(pictureFormat, session.pictureFormat) &&
                Objects.equals(advertisingPlatform, session.advertisingPlatform) &&
                Objects.equals(viewer, session.viewer) &&
                Objects.equals(creationTime, session.creationTime) &&
                Objects.equals(closingTime, session.closingTime);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, partner, pictureFormat, advertisingPlatform, displaysCounter, clickCounter, viewer, creationTime, closingTime);
    }
}
