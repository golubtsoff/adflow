package entity.statistics;

import entity.users.partner.AdvertisingPlatform;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "SESSIONS")
@SecondaryTable(name = "VIEWERS")
public class Session {

    public static final String ID = "ID";
    public static final String ADVERTISING_PLATFORM_ID = "ADVERTISING_PLATFORM_ID";
    public static final String DISPLAYS_COUNTER = "DISPLAYS_COUNTER";
    public static final String CLICK_COUNTER = "CLICK_COUNTER";
    public static final String NAME = "NAME";
    public static final String IP = "IP";
    public static final String CREATION_TIME = "CREATION_TIME";
    public static final String CLOSING_TIME = "CLOSING_TIME";

    @Id
    @Column(name = ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(name = ADVERTISING_PLATFORM_ID)
    private AdvertisingPlatform advertisingPlatform;

    @Column(name = DISPLAYS_COUNTER)
    private int displaysCounter;

    @Column(name = CLICK_COUNTER)
    private int clickCounter;

    @AttributeOverrides({
            @AttributeOverride(name=NAME, column=@Column(table="VIEWERS")),
            @AttributeOverride(name=IP, column=@Column(table="VIEWERS"))
    })
    private Viewer viewer;

    @Column(name = CREATION_TIME)
    private LocalDateTime creationTime;

    @Column(name = CLOSING_TIME)
    private LocalDateTime closingTime;

    public Session() {
    }

    public Session(
            AdvertisingPlatform advertisingPlatform,
            int displaysCounter,
            int clickCounter,
            Viewer viewer,
            LocalDateTime creationTime,
            LocalDateTime closingTime) {
        this(null, advertisingPlatform, displaysCounter,
                clickCounter, viewer, creationTime, closingTime);
    }

    public Session(
            Long id,
            AdvertisingPlatform advertisingPlatform,
            int displaysCounter,
            int clickCounter,
            Viewer viewer,
            LocalDateTime creationTime,
            LocalDateTime closingTime) {
        this.id = id;
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
                Objects.equals(advertisingPlatform, session.advertisingPlatform) &&
                Objects.equals(viewer, session.viewer) &&
                Objects.equals(creationTime, session.creationTime) &&
                Objects.equals(closingTime, session.closingTime);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, advertisingPlatform, displaysCounter, clickCounter, viewer, creationTime, closingTime);
    }
}
