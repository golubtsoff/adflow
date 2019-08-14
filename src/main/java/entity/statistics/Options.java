package entity.statistics;

import entity.users.partner.Partner;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Table(name = "OPTIONS")
public class Options {

    public static final int DURATION_SHOW_DEFAULT = 60;
    public static final String ID = "ID";
    public static final String DURATION_SHOW = "DURATION_SHOW";

    @Id
    @Column(name = ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = DURATION_SHOW)
    private int durationShow;

    public Options(){}

    public Options(int durationShow){
        this.durationShow = durationShow;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getDurationShow() {
        return durationShow;
    }

    public void setDurationShow(int durationShow) {
        this.durationShow = durationShow;
    }
}
