package entity.users.customer;

import entity.users.Action;
import entity.users.Status;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "campaigns")
public class Campaign {

    public static final String ID = "id";
    public static final String CUSTOMER_ID = "customer_id";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String URL = "url";
    public static final String PICTURES = "pictures";
    public static final String DAILY_BUDGET = "daily_budget";
    public static final String CPM_RATE = "cpm_rate";
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
    @JoinColumn(name = CUSTOMER_ID)
    private Customer customer;

    @Column(name = TITLE)
    private String title;

    @Column(name = DESCRIPTION)
    private String description;

    @Column(name = URL)
    private String pathOnClick;

    @ElementCollection
    @CollectionTable(name = PICTURES)
    private Set<Picture> pictures = new HashSet<>();

    @Column(name = DAILY_BUDGET)
    private BigDecimal dailyBudget;

    @Column(name = CPM_RATE)
    private BigDecimal cpmRate;

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

    public Campaign() {}

    public Campaign(Customer customer){
        this(customer, null, null, null, null, null);
    }

    public Campaign(
            Customer customer,
            String title,
            String description,
            String pathOnClick,
            BigDecimal dailyBudget,
            BigDecimal cpmRate
    ) {
        this(customer, title, description, pathOnClick, dailyBudget, cpmRate, Action.PAUSE, Status.CHECKING);
    }

    public Campaign(
            Customer customer,
            String title,
            String description,
            String pathOnClick,
            BigDecimal dailyBudget,
            BigDecimal cpmRate,
            Action action,
            Status status
    ) {
        this(null, customer, title, description, pathOnClick, dailyBudget, cpmRate, action, status);
    }

    public Campaign(
            Long id,
            Customer customer,
            String title,
            String description,
            String pathOnClick,
            BigDecimal dailyBudget,
            BigDecimal cpmRate,
            Action action,
            Status status
    ) {
        this.id = id;
        this.customer = customer;
        this.title = title;
        this.description = description;
        this.pathOnClick = pathOnClick;
        this.dailyBudget = dailyBudget;
        this.cpmRate = cpmRate;
        this.creationDate = LocalDateTime.now();
        this.action = action;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
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

    public String getPathOnClick() {
        return pathOnClick;
    }

    public void setPathOnClick(String pathOnClick) {
        this.pathOnClick = pathOnClick;
    }

    public BigDecimal getDailyBudget() {
        return dailyBudget;
    }

    public void setDailyBudget(BigDecimal dailyBudget) {
        this.dailyBudget = dailyBudget;
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

    public Set<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(Set<Picture> pictures) {
        this.pictures = pictures;
    }

    public void addPicture(Picture picture){
        this.pictures.add(picture);
    }

    public void removePicture(Picture picture){
        this.pictures.remove(picture);
    }

    @Override
    public String toString() {
        return "Campaign{" +
                "id=" + id +
                ", customer_id=" + customer.getId() +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", pathOnClick='" + pathOnClick + '\'' +
                ", pictures=" + pictures +
                ", dailyBudget=" + dailyBudget +
                ", cpmRate=" + cpmRate +
                ", creationDate=" + creationDate +
                ", removedDate=" + removedDate +
                ", action=" + action +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Campaign campaign = (Campaign) o;
        return Objects.equals(id, campaign.id) &&
                Objects.equals(customer, campaign.customer) &&
                Objects.equals(title, campaign.title) &&
                Objects.equals(description, campaign.description) &&
                Objects.equals(pathOnClick, campaign.pathOnClick) &&
                Objects.equals(dailyBudget, campaign.dailyBudget) &&
                Objects.equals(cpmRate, campaign.cpmRate) &&
                Objects.equals(creationDate, campaign.creationDate) &&
                action == campaign.action &&
                status == campaign.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customer, title, description, pathOnClick, dailyBudget, cpmRate, creationDate, action, status);
    }
}
