package entity.statistics;

import entity.users.customer.Campaign;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.util.Objects;

import javax.persistence.*;

@Entity
@Table(name = "CAMPAIGN_STATISTICS")
public class CampaignStatistics extends AbstractStatistics {

    public static final String CAMPAIGN_ID = "CAMPAIGN_ID";

    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(name = CAMPAIGN_ID)
    private Campaign campaign;

    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    @Override
    public String toString() {
        return "CampaignStatistics{" +
                "campaign=" + campaign +
                ", id=" + id +
                ", date=" + date +
                ", displaysCount=" + displaysCount +
                ", clickCount=" + clickCount +
                ", cost=" + cost +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CampaignStatistics)) return false;
        CampaignStatistics that = (CampaignStatistics) o;
        return Objects.equals(getCampaign(), that.getCampaign()) &&
                Objects.equals(getDate(), that.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDate(), getCampaign());
    }
}
