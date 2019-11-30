package rest.statistics.dto;

import java.time.LocalDate;
import java.util.List;

public class DetailStatisticsDto extends StatisticsDto {
    private List<GroupByPeriodsStatistics> groupByPeriodsStatisticsList;

    public DetailStatisticsDto(){}

    public DetailStatisticsDto(LocalDate from, LocalDate to,
           TotalStatistics totalStatistics,
           List<GroupByPeriodsStatistics> groupByPeriodsStatisticsList
           ) {
        super(from ,to, totalStatistics);
        this.groupByPeriodsStatisticsList = groupByPeriodsStatisticsList;

    }

    public List<GroupByPeriodsStatistics> getGroupByPeriodsStatisticsList() {
        return groupByPeriodsStatisticsList;
    }

    public void setGroupByPeriodsStatisticsList(List<GroupByPeriodsStatistics> groupByPeriodsStatisticsList) {
        this.groupByPeriodsStatisticsList = groupByPeriodsStatisticsList;
    }

    @Override
    public String toString() {
        return "DetailStatisticsDto{" +
                "groupByPeriodsStatisticsList=" + groupByPeriodsStatisticsList +
                ", from=" + from +
                ", to=" + to +
                ", totalStatistics=" + totalStatistics +
                '}';
    }


}
