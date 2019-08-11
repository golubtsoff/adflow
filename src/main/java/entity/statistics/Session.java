package entity.statistics;

import entity.users.partner.Platform;
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
    public static final String PLATFORM_ID = "PLATFORM_ID";
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
    @JoinColumn(name = PLATFORM_ID)
    private Platform platform;

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
            Platform platform,
            int displaysCounter,
            int clickCounter,
            Viewer viewer,
            LocalDateTime creationTime) {
        this(null, platform, displaysCounter, clickCounter, viewer, creationTime);
    }

    public Session(
            Long id,
            Platform platform,
            int displaysCounter,
            int clickCounter,
            Viewer viewer,
            LocalDateTime creationTime
            ) {
        this.id = id;
        this.platform = platform;
        this.displaysCounter = displaysCounter;
        this.clickCounter = clickCounter;
        this.viewer = viewer;
        this.creationTime = creationTime;
        this.closingTime = creationTime;
    }

    public Long getId() {
        return id;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
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
                ", platform=" + platform +
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
                Objects.equals(platform, session.platform) &&
                Objects.equals(viewer, session.viewer) &&
                Objects.equals(creationTime, session.creationTime) &&
                Objects.equals(closingTime, session.closingTime);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, platform, displaysCounter, clickCounter, viewer, creationTime, closingTime);
    }
}
