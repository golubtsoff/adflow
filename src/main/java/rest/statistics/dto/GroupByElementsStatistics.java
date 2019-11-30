package rest.statistics.dto;

import java.math.BigDecimal;
import java.util.Objects;

public class GroupByElementsStatistics {
    private long elementId;
    private String title;
    private int displaysCount;
    private int clickCount;
    private BigDecimal cost;

    public GroupByElementsStatistics(){}

    public GroupByElementsStatistics(long elementId, String title,
             int displaysCount, int clickCount, BigDecimal cost) {
        this.elementId = elementId;
        this.title = title;
        this.displaysCount = displaysCount;
        this.clickCount = clickCount;
        this.cost = cost;
    }

    public long getElementId() {
        return elementId;
    }

    public void setElementId(long elementId) {
        this.elementId = elementId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
        return "ShortStatisticsDto{" +
                "id=" + elementId +
                ", title='" + title + '\'' +
                ", displaysCount=" + displaysCount +
                ", clickCount=" + clickCount +
                ", cost=" + cost +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupByElementsStatistics that = (GroupByElementsStatistics) o;
        return elementId == that.elementId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(elementId);
    }
}
