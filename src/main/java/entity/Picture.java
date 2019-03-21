package entity;

import entity.campaign.Campaign;
import entity.partner.Partner;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Parent;

import javax.persistence.*;
import java.util.Objects;

@Embeddable
public class Picture {

    @OneToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(name = "PICTURE_FORMAT_ID")
    private PictureFormat pictureFormat;

    @Column(name = "FILENAME")
    private String fileName;

    public Picture(){}

    public Picture(String filename, PictureFormat pictureFormat){
        this.fileName = filename;
        this.pictureFormat = pictureFormat;
    }

    public PictureFormat getPictureFormat() {
        return pictureFormat;
    }

    public void setPictureFormat(PictureFormat pictureFormat) {
        this.pictureFormat = pictureFormat;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "Picture{" +
                "pictureFormat=" + pictureFormat +
                ", fileName='" + fileName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Picture)) return false;
        Picture picture = (Picture) o;
        return Objects.equals(getPictureFormat(), picture.getPictureFormat()) &&
                Objects.equals(getFileName(), picture.getFileName());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getPictureFormat(), getFileName());
    }
}
