package entity.statistics;

import entity.users.customer.Campaign;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import static entity.statistics.Options.DURATION_SHOW;

@Entity
@Table(name = "requests")
public class Request {

    public static final String ID = "id";
    public static final String SESSION_ID = "session_id";
    public static final String CAMPAIGN_ID = "campaign_id";
    public static final String CREATION_TIME = "creation_time";
    public static final String CLICK_ON = "click_on";
    public static final String CAMPAIGN_CPM_RATE = "campaign_cpm_rate";
    public static final String PLATFORM_CPM_RATE = "platform_cpm_rate";
    private static final String ACTUAL_SHOW_TIME = "actual_show_time";
    private static final int NOT_SHOWN = 0;

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

    @Column(name = CREATION_TIME)
    private LocalDateTime creationTime;

    @Column(name = ACTUAL_SHOW_TIME)
    private int actualShowTime;

    @Column(name = DURATION_SHOW)
    private int durationShow;

    @Column(name = CLICK_ON)
    private boolean clickOn;

    @Column(name = CAMPAIGN_CPM_RATE)
    private BigDecimal campaignCpmRate;

    @Column(name = PLATFORM_CPM_RATE)
    private BigDecimal platformCpmRate;

    public Request(){}

    Request(
            Session session,
            Campaign campaign,
            int durationShow
    ) {
        this.session = session;
        this.platformCpmRate = session.getPlatform().getCpmRate();
        this.campaign = campaign;
        this.campaignCpmRate = campaign.getCpmRate();
        this.creationTime = session.getClosingTime();
        this.durationShow = durationShow;
        this.actualShowTime = NOT_SHOWN;
    }

    public void updateRequest(boolean clickOn, Integer actualShowTime){
        if (clickOn){
            this.clickOn = true;
            session.setClickCounter(session.getClickCounter() + 1);
        }
        if (actualShowTime != null){
            if (actualShowTime <= this.getDurationShow()
                && actualShowTime > NOT_SHOWN){
                this.actualShowTime = actualShowTime;
            } else if (actualShowTime > this.durationShow){
                this.actualShowTime = this.durationShow;
            }
        }
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

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public int getDurationShow() {
        return durationShow;
    }

    public void setDurationShow(int durationShow) {
        this.durationShow = durationShow;
    }

    public int getActualShowTime() {
        return actualShowTime;
    }

    public void setActualShowTime(int actualShowTime) {
        this.actualShowTime = actualShowTime;
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
                ", creationTime=" + creationTime +
                ", actualShowTime=" + actualShowTime +
                ", durationShow=" + durationShow +
                ", clickOn=" + clickOn +
                ", campaignCpmRate=" + campaignCpmRate +
                ", platformCpmRate=" + platformCpmRate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request request = (Request) o;
        return Objects.equals(id, request.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
