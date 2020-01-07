package entity.users.customer;

import entity.users.AdvertisingEntity;
import entity.users.Action;
import entity.users.Status;
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
public class Campaign extends AdvertisingEntity {

    public static final String CUSTOMER_ID = "customer_id";
    public static final String URL = "url";
    public static final String PICTURES = "pictures";
    public static final String DAILY_BUDGET = "daily_budget";

    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(name = CUSTOMER_ID)
    private Customer customer;

    @Column(name = URL)
    private String pathOnClick;

    @ElementCollection
    @CollectionTable(name = PICTURES)
    private Set<Picture> pictures = new HashSet<>();

    @Column(name = DAILY_BUDGET)
    private BigDecimal dailyBudget;

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

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
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
        return Objects.equals(id, campaign.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
