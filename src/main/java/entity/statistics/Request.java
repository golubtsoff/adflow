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
@Table(name = "REQUESTS")
public class Request {

    public static final String ID = "ID";
    public static final String SESSION_ID = "SESSION_ID";
    public static final String CAMPAIGN_ID = "CAMPAIGN_ID";
    public static final String CREATION_TIME = "CREATION_TIME";
    public static final String UPDATED_TIME = "UPDATED_TIME";
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

    @Column(name = CREATION_TIME)
    private LocalDateTime creationTime;

    @Column(name = UPDATED_TIME)
    private LocalDateTime updatedTime;

    @Column(name = DURATION_SHOW)
    private int durationShow;

    @Column(name = CONFIRM_SHOW)
    private boolean confirmShow;

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
    }

    public void updateRequest(boolean confirmShow, boolean clickOn){
        if (confirmShow){
            this.confirmShow = true;
            session.setDisplaysCounter(session.getDisplaysCounter() + 1);
        }
        if (clickOn){
            this.clickOn = true;
            session.setClickCounter(session.getClickCounter() + 1);
        }
        this.updatedTime = LocalDateTime.now();
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

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    public int getDurationShow() {
        return durationShow;
    }

    public void setDurationShow(int durationShow) {
        this.durationShow = durationShow;
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
                ", creationTime=" + creationTime +
                ", updatedTime=" + updatedTime +
                ", durationShow=" + durationShow +
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
        return getDurationShow() == request.getDurationShow() &&
                isConfirmShow() == request.isConfirmShow() &&
                isClickOn() == request.isClickOn() &&
                Objects.equals(getId(), request.getId()) &&
                Objects.equals(getSession(), request.getSession()) &&
                Objects.equals(getCampaign(), request.getCampaign()) &&
                Objects.equals(getCreationTime(), request.getCreationTime()) &&
                Objects.equals(getUpdatedTime(), request.getUpdatedTime()) &&
                Objects.equals(getCampaignCpmRate(), request.getCampaignCpmRate()) &&
                Objects.equals(getPlatformCpmRate(), request.getPlatformCpmRate());
    }

    @Override
    public int hashCode() {

        return Objects.hash(
                getId(),
                getSession(),
                getCampaign(),
                getCreationTime(),
                getUpdatedTime(),
                getDurationShow(),
                isConfirmShow(),
                isClickOn(),
                getCampaignCpmRate(),
                getPlatformCpmRate()
        );
    }
}
