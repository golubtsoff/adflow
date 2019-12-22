package rest.statistics.dto;

import java.math.BigDecimal;
import java.util.Objects;

public class GroupByElementsStatistics extends GroupStatistics {
    private long elementId;
    private String title;

    public GroupByElementsStatistics(){
        super();
    }

    public GroupByElementsStatistics(long elementId, String title,
             int displaysCount, int clickCount, BigDecimal cost, int actualShowTime) {
        super(displaysCount, clickCount, cost, actualShowTime);
        this.elementId = elementId;
        this.title = title;
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

    @Override
    public String toString() {
        return "GroupByElementsStatistics{" +
                "elementId=" + elementId +
                ", title='" + title + '\'' +
                ", displaysCount=" + displaysCount +
                ", clickCount=" + clickCount +
                ", cost=" + cost +
                ", actualShowTime=" + actualShowTime +
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
