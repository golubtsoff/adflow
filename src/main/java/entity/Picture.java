package entity;

import entity.campaign.Campaign;
import entity.partner.Partner;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Table(name = "PICTURES")
public class Picture {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(name = "CAMPAIGN_ID")
    private Campaign campaign;

    @OneToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(name = "PICTURE_FORMAT_ID")
    private PictureFormat pictureFormat;

    @Column(name = "FILENAME")
    private String fileName;
}
