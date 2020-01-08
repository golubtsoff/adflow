package rest.statistics.dto;

import java.time.LocalDate;

public abstract class StatisticsDto {
    protected LocalDate from;
    protected LocalDate to;
    protected TotalStatistics totalStatistics;

    protected StatisticsDto(){}

    protected StatisticsDto(LocalDate from, LocalDate to, TotalStatistics totalStatistics) {
        this.from = from;
        this.to = to;
        this.totalStatistics = totalStatistics;
    }

    public LocalDate getFrom() {
        return from;
    }

    public void setFrom(LocalDate from) {
        this.from = from;
    }

    public LocalDate getTo() {
        return to;
    }

    public void setTo(LocalDate to) {
        this.to = to;
    }

    public TotalStatistics getTotalStatistics() {
        return totalStatistics;
    }

    public void setTotalStatistics(TotalStatistics totalStatistics) {
        this.totalStatistics = totalStatistics;
    }

    @Override
    public String toString() {
        return "StatisticsDto{" +
                "from=" + from +
                ", to=" + to +
                ", totalStatistics=" + totalStatistics +
                '}';
    }

}
