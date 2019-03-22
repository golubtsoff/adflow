package entity.statistics;

import entity.users.customer.Campaign;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "REQUESTS")
public class Request {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(name = "SESSION_ID")
    private Session session;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(name = "CAMPAIGN_ID")
    private Campaign campaign;

    private LocalDate date;

    private boolean confirmShow;

    private boolean clickOn;

    private BigDecimal campaignCpmRate;

    private BigDecimal platformCpmRate;

    public Long getId() {
        return id;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public boolean isConfirmShow() {
        return confirmShow;
    }

    public void setConfirmShow(boolean confirmShow) {
        this.confirmShow = confirmShow;
    }

    public boolean isClickOn() {
        return clickOn;
    }

    public void setClickOn(boolean clickOn) {
        this.clickOn = clickOn;
    }

    public BigDecimal getCampaignCpmRate() {
        return campaignCpmRate;
    }

    public void setCampaignCpmRate(BigDecimal campaignCpmRate) {
        this.campaignCpmRate = campaignCpmRate;
    }

    public BigDecimal getPlatformCpmRate() {
        return platformCpmRate;
    }

    public void setPlatformCpmRate(BigDecimal platformCpmRate) {
        this.platformCpmRate = platformCpmRate;
    }

    @Override
    public String toString() {
        return "Request{" +
                "id=" + id +
                ", session=" + session +
                ", campaign=" + campaign +
                ", date=" + date +
                ", confirmShow=" + confirmShow +
                ", clickOn=" + clickOn +
                ", campaignCpmRate=" + campaignCpmRate +
                ", platformCpmRate=" + platformCpmRate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Request)) return false;
        Request request = (Request) o;
        return Objects.equals(getId(), request.getId());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId());
    }
}
