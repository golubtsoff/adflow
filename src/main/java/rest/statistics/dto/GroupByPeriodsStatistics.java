package rest.statistics.dto;

import java.math.BigDecimal;

public class GroupByPeriodsStatistics extends GroupStatistics {
    private String period;

    public GroupByPeriodsStatistics(){}

    public GroupByPeriodsStatistics(
            String period,
            int displaysCount,
            int clickCount,
            BigDecimal cost,
            int actualShowTime) {
        super(displaysCount, clickCount, cost, actualShowTime);
        this.period = period;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    @Override
    public String toString() {
        return "GroupByPeriodsStatistics{" +
                "period='" + period + '\'' +
                ", displaysCount=" + displaysCount +
                ", clickCount=" + clickCount +
                ", cost=" + cost +
                ", actualShowTime=" + actualShowTime +
                '}';
    }
}
