package rest.statistics.dto;

import java.math.BigDecimal;

public abstract class GroupStatistics {
    protected int displaysCount;
    protected int clickCount;
    protected BigDecimal cost;
    protected int actualShowTime;

    protected GroupStatistics(){}

    protected GroupStatistics(int displaysCount, int clickCount, BigDecimal cost, int actualShowTime) {
        this.displaysCount = displaysCount;
        this.clickCount = clickCount;
        this.cost = cost;
        this.actualShowTime = actualShowTime;
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

    public int getActualShowTime() {
        return actualShowTime;
    }

    public void setActualShowTime(int actualShowTime) {
        this.actualShowTime = actualShowTime;
    }

    @Override
    public String toString() {
        return "GroupStatistics{" +
                "displaysCount=" + displaysCount +
                ", clickCount=" + clickCount +
                ", cost=" + cost +
                ", actualShowTime=" + actualShowTime +
                '}';
    }

}
