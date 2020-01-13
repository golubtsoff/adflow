package rest.statistics.dto;

import java.math.BigDecimal;

public class TotalStatistics extends GroupStatistics {

    private final String title = "Total";

    public TotalStatistics(){
        super();
    }

    public TotalStatistics(
            int displaysCount,
            int clickCount,
            BigDecimal cost,
            int actualShowTime) {
        super(displaysCount, clickCount, cost, actualShowTime);
    }

    @Override
    public String toString() {
        return "TotalStatistics{" +
                "title='" + title + '\'' +
                ", displaysCount=" + displaysCount +
                ", clickCount=" + clickCount +
                ", cost=" + cost +
                ", actualShowTime=" + actualShowTime +
                '}';
    }

}
