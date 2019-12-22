package rest.statistics.dto;

import java.time.LocalDate;
import java.util.List;

public class ShortStatisticsDto extends StatisticsDto {
    private List<GroupByElementsStatistics> groupByElementsStatisticsList;

    public ShortStatisticsDto() {
        super();
    }

    public ShortStatisticsDto(LocalDate from, LocalDate to,
            TotalStatistics totalStatistics,
            List<GroupByElementsStatistics> groupByElementsStatisticsList) {
        super(from, to, totalStatistics);
        this.groupByElementsStatisticsList = groupByElementsStatisticsList;
    }

    public List<GroupByElementsStatistics> getGroupByElementsStatisticsList() {
        return groupByElementsStatisticsList;
    }

    public void setGroupByElementsStatisticsList(List<GroupByElementsStatistics> groupByElementsStatisticsList) {
        this.groupByElementsStatisticsList = groupByElementsStatisticsList;
    }

    @Override
    public String toString() {
        return "ShortStatisticsDto{" +
                "groupByElementsStatisticsList=" + groupByElementsStatisticsList +
                ", from=" + from +
                ", to=" + to +
                ", totalStatistics=" + totalStatistics +
                '}';
    }
}
