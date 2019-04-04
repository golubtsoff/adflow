package entity.statistics;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@MappedSuperclass
public abstract class AbstractStatistics {

    public static final String ID = "ID";

    @Id
    @Column(name = ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    protected LocalDate date;
    protected int displaysCount;
    protected int clickCount;
    protected BigDecimal cost;

    protected AbstractStatistics(){}

    protected AbstractStatistics(LocalDate date){
        this(date, 0, 0, BigDecimal.valueOf(0));
    }

    protected AbstractStatistics(LocalDate date, int displaysCount, int clickCount, BigDecimal cost){
        this.date = date;
        this.displaysCount = displaysCount;
        this.clickCount = clickCount;
        this.cost = cost;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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
}
