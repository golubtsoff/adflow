package entity.users;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class AbstractCampaignPlatform {

    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String CPM_RATE = "cpm_rate";
    public static final String CREATION_DATE = "creation_date";
    public static final String REMOVED_DATE = "removed_date";
    public static final String ACTION = "action";
    public static final String STATUS = "status";

    @Id
    @Column(name = ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = TITLE)
    protected String title;

    @Column(name = DESCRIPTION)
    protected String description;

    @Column(name = CPM_RATE)
    protected BigDecimal cpmRate;

    @Column(name = CREATION_DATE)
    protected LocalDateTime creationDate;

    @Column(name = REMOVED_DATE)
    protected LocalDateTime removedDate;

    @Column(name = ACTION)
    @Enumerated(EnumType.STRING)
    protected Action action;

    @Column(name = STATUS)
    @Enumerated(EnumType.STRING)
    protected Status status;

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

    public Action getAction() {
        return action;
    }

    public void setAction(Action trigger) {
        this.action = trigger;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
        if (status == Status.REMOVED){
            this.setRemovedDate(LocalDateTime.now());
            this.setAction(Action.STOP);
        } else if (status == Status.CHECKING || status == Status.LOCKED){
            if (this.getAction() == Action.RUN){
                this.setAction(Action.PAUSE);
            }
        }
    }
}
