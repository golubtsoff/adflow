package entity.statistics;

import entity.users.customer.Campaign;
import entity.users.partner.Platform;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "REQUESTS")
public class Request {

    public static final String ID = "ID";
    public static final String SESSION_ID = "SESSION_ID";
    public static final String CAMPAIGN_ID = "CAMPAIGN_ID";
    public static final String DATE = "DATE";
    public static final String CONFIRM_SHOW = "CONFIRM_SHOW";
    public static final String CLICK_ON = "CLICK_ON";
    public static final String CAMPAIGN_CPM_RATE = "CAMPAIGN_CPM_RATE";
    public static final String PLATFORM_CPM_RATE = "PLATFORM_CPM_RATE";

    @Id
    @Column(name = ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(name = SESSION_ID)
    private Session session;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(name = CAMPAIGN_ID)
    private Campaign campaign;

    @Column(name = DATE)
    private LocalDateTime date;

    @Column(name = CONFIRM_SHOW)
    private boolean confirmShow;

    @Column(name = CLICK_ON)
    private boolean clickOn;

    @Column(name = CAMPAIGN_CPM_RATE)
    private BigDecimal campaignCpmRate;

    @Column(name = PLATFORM_CPM_RATE)
    private BigDecimal platformCpmRate;

    public Request(){}

    public Request(
            Session session,
            Campaign campaign
    ) {
        this.session = session;
        this.platformCpmRate = session.getPlatform().getCpmRate();
        this.campaign = campaign;
        this.campaignCpmRate = campaign.getCpmRate();
        this.date = LocalDateTime.now();
    }

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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
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
        return isConfirmShow() == request.isConfirmShow() &&
                isClickOn() == request.isClickOn() &&
                Objects.equals(getId(), request.getId()) &&
                Objects.equals(getSession(), request.getSession()) &&
                Objects.equals(getCampaign(), request.getCampaign()) &&
                Objects.equals(getDate(), request.getDate()) &&
                Objects.equals(getCampaignCpmRate(), request.getCampaignCpmRate()) &&
                Objects.equals(getPlatformCpmRate(), request.getPlatformCpmRate());
    }

    @Override
    public int hashCode() {

        return Objects.hash(
                getId(),
                getSession(),
                getCampaign(),
                getDate(),
                isConfirmShow(),
                isClickOn(),
                getCampaignCpmRate(),
                getPlatformCpmRate());
    }
}
