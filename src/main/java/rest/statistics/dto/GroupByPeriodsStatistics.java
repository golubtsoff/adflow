package rest.statistics.dto;

import java.math.BigDecimal;
import java.util.Objects;

public class GroupByPeriodsStatistics {
    private String period;
    private int displaysCount;
    private int clickCount;
    private BigDecimal cost;

    public GroupByPeriodsStatistics(){}

    public GroupByPeriodsStatistics(String period, int displaysCount, int clickCount, BigDecimal cost) {
        this.period = period;
        this.displaysCount = displaysCount;
        this.clickCount = clickCount;
        this.cost = cost;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public int getDisplaysCount() {
        return displaysCount;
    }

    public void setDisplaysCount(int displaysCount) {
        this.displaysCount = displaysCount;
    }

    public int getClickCount() {
        return clickCount;
    }

    public void setClickCount(int clickCount) {
        this.clickCount = clickCount;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "DetailStatisticsDto{" +
                "period='" + period + '\'' +
                ", displaysCount=" + displaysCount +
                ", clickCount=" + clickCount +
                ", cost=" + cost +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupByPeriodsStatistics that = (GroupByPeriodsStatistics) o;
        return Objects.equals(period, that.period);
    }

    @Override
    public int hashCode() {
        return Objects.hash(period);
    }
}
