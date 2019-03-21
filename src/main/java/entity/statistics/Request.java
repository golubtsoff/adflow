package entity.statistics;

import entity.users.customer.Campaign;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "REQUESTS")
public class Request {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Session session;

    private Campaign campaign;

    private LocalDate date;

    private boolean confirmShow;

    private boolean clickOn;

    private BigDecimal campaignCpmRate;

    private BigDecimal platformCpmRate;
}
