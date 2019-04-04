package entity.statistics;

import entity.users.partner.AdvertisingPlatform;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.util.Objects;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "ADVERTISING_PLATFORM_STATISTICS")
public class AdvertisingPlatformStatistics extends AbstractStatistics {

    public static final String ADVERTISING_PLATFORM_ID = "ADVERTISING_PLATFORM_ID";

    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(name = ADVERTISING_PLATFORM_ID)
    private AdvertisingPlatform platform;

    public AdvertisingPlatformStatistics(){
        super();
    }

    public AdvertisingPlatformStatistics(AdvertisingPlatform platform, LocalDate date){
        this(platform, date, 0, 0, BigDecimal.valueOf(0));
    }

    public AdvertisingPlatformStatistics(AdvertisingPlatform platform, LocalDate date, int displaysCount, int clickCount, BigDecimal cost){
        super(date, displaysCount, clickCount, cost);
        this.platform = platform;
    }

    public AdvertisingPlatform getPlatform() {
        return platform;
    }

    public void setPlatform(AdvertisingPlatform platform) {
        this.platform = platform;
    }

    @Override
    public String toString() {
        return "AdvertisingPlatformStatistics{" +
                "platform=" + platform +
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
        if (!(o instanceof AdvertisingPlatformStatistics)) return false;
        AdvertisingPlatformStatistics that = (AdvertisingPlatformStatistics) o;
        return Objects.equals(getPlatform(), that.getPlatform()) &&
                Objects.equals(getDate(), that.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDate(), getPlatform());
    }
}
