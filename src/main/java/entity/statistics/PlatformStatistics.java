package entity.statistics;

import entity.users.partner.Platform;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.util.Objects;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "PLATFORM_STATISTICS")
public class PlatformStatistics extends AbstractStatistics {

    public static final String PLATFORM_ID = "PLATFORM_ID";

    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(name = PLATFORM_ID)
    private Platform platform;

    public PlatformStatistics(){
        super();
    }

    public PlatformStatistics(Platform platform, LocalDate date){
        this(platform, date, 0, 0, BigDecimal.valueOf(0));
    }

    public PlatformStatistics(Platform platform, LocalDate date, int displaysCount, int clickCount, BigDecimal cost){
        super(date, displaysCount, clickCount, cost);
        this.platform = platform;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    @Override
    public String toString() {
        return "PlatformStatistics{" +
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
        if (!(o instanceof PlatformStatistics)) return false;
        PlatformStatistics that = (PlatformStatistics) o;
        return Objects.equals(getPlatform(), that.getPlatform()) &&
                Objects.equals(getDate(), that.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDate(), getPlatform());
    }
}
