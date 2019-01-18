package entity.campaign;

import entity.Customer;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "campaigns")
public class Campaign {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(name = "CUSTOMER_ID")
    private Customer customer;

    @Column(name = "TITLE")
    private String Title;

    @Column(name = "DESCRIPTION")
    private String Description;

    @Column(name = "URL")
    private String pathOnClick;

    @Column(name = "DAILY_BUDGET")
    private BigDecimal dailyBudget;

    @Column(name = "CPM_RATE")
    private BigDecimal cpmRate;

    @Column(name = "CREATION_DATE")
    private LocalDateTime creationDate;

    @Column(name = "TRIGGER")
    @Enumerated(EnumType.STRING)
    private Trigger trigger;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private Status status;

    public Campaign() {}

    public Campaign(
            Customer customer,
            String title,
            String description,
            String pathOnClick,
            BigDecimal dailyBudget,
            BigDecimal cpmRate,
            LocalDateTime creationDate,
            Trigger trigger,
            Status status
    ) {
        this(null, customer, title, description, pathOnClick, dailyBudget, cpmRate, creationDate, trigger, status);
    }

    public Campaign(
            Long id,
            Customer customer,
            String title,
            String description,
            String pathOnClick,
            BigDecimal dailyBudget,
            BigDecimal cpmRate,
            LocalDateTime creationDate,
            Trigger trigger,
            Status status
    ) {
        this.id = id;
        this.customer = customer;
        Title = title;
        Description = description;
        this.pathOnClick = pathOnClick;
        this.dailyBudget = dailyBudget;
        this.cpmRate = cpmRate;
        this.creationDate = creationDate;
        this.trigger = trigger;
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
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
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

    public Trigger getTrigger() {
        return trigger;
    }

    public void setTrigger(Trigger trigger) {
        this.trigger = trigger;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Campaign{" +
                "id=" + id +
                ", customer=" + customer +
                ", Title='" + Title + '\'' +
                ", Description='" + Description + '\'' +
                ", pathOnClick='" + pathOnClick + '\'' +
                ", dailyBudget=" + dailyBudget +
                ", cpmRate=" + cpmRate +
                ", creationDate=" + creationDate +
                ", trigger=" + trigger +
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
                Objects.equals(Title, campaign.Title) &&
                Objects.equals(Description, campaign.Description) &&
                Objects.equals(pathOnClick, campaign.pathOnClick) &&
                Objects.equals(dailyBudget, campaign.dailyBudget) &&
                Objects.equals(cpmRate, campaign.cpmRate) &&
                Objects.equals(creationDate, campaign.creationDate) &&
                trigger == campaign.trigger &&
                status == campaign.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customer, Title, Description, pathOnClick, dailyBudget, cpmRate, creationDate, trigger, status);
    }
}
