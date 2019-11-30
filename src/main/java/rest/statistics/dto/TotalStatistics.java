package rest.statistics.dto;

import java.math.BigDecimal;
import java.util.Objects;

public class TotalStatistics {

    private final String title = "Total";
    private int displaysCount;
    private int clickCount;
    private BigDecimal cost;

    public TotalStatistics(){}

    public TotalStatistics(int displaysCount, int clickCount, BigDecimal cost) {
        this.displaysCount = displaysCount;
        this.clickCount = clickCount;
        this.cost = cost;
    }

    public String getTitle() {
        return title;
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
        return "TotalStatisticsDto{" +
                "title='" + title + '\'' +
                ", displaysCount=" + displaysCount +
                ", clickCount=" + clickCount +
                ", cost=" + cost +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TotalStatistics that = (TotalStatistics) o;
        return displaysCount == that.displaysCount &&
                clickCount == that.clickCount &&
                Objects.equals(cost, that.cost);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, displaysCount, clickCount, cost);
    }
}
