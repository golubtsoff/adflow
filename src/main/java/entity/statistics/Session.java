package entity.statistics;

import entity.users.customer.Campaign;
import entity.users.partner.Platform;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "sessions")
@SecondaryTable(name = "viewers")
public class Session {

    public static final String ID = "id";
    public static final String PLATFORM_ID = "platform_id";
    public static final String DISPLAYS_COUNTER = "displays_counter";
    public static final String CLICK_COUNTER = "click_counter";
    public static final String NAME = "name";
    public static final String IP = "ip";
    public static final String CREATION_TIME = "creation_time";
    public static final String CLOSING_TIME = "closing_time";

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

    public Session(Platform platform, Viewer viewer) {
        this(null, platform, viewer);
    }

    public Session(
            Long id,
            Platform platform,
            Viewer viewer
    ) {
        this.id = id;
        this.platform = platform;
        this.viewer = viewer;
        this.creationTime = LocalDateTime.now();
        this.closingTime = creationTime;
    }

    public Request getRequestInstance(Campaign campaign, int durationShow){
        setClosingTime(LocalDateTime.now());
        setDisplaysCounter(++displaysCounter);
        return new Request(this, campaign, durationShow);
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
        return Objects.equals(id, session.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
