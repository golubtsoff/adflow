package entity.statistics;

import javax.persistence.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Entity
@Table(name = "options")
public class Options {

    public static int durationShowDefault;
    public static final String ID = "ID";
    public static final String DURATION_SHOW = "DURATION_SHOW";

    private static final String PATH = "/adv_options.properties";
    private static final String DURATION_SHOW_DEFAULT_NAME = "duration_show_default";
    private static final String DURATION_SHOW_DEFAULT_VALUE = "61";

    static{
        try (InputStream is = Options.class.getResourceAsStream(PATH)) {
            Properties props = new Properties();
            props.load(is);
            durationShowDefault = Integer.parseInt(props.getProperty(
                    DURATION_SHOW_DEFAULT_NAME,
                    DURATION_SHOW_DEFAULT_VALUE
            ));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
