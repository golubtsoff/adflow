package dao.impl;

import dao.AbstractDao;
import dao.DbAssistant;
import entity.users.PictureFormat;

import java.util.List;

import static entity.users.PictureFormat.CAN_BE_USED;
import static entity.users.PictureFormat.HEIGHT;
import static entity.users.PictureFormat.WIDTH;

public class PictureFormatDao extends AbstractDao<PictureFormat> {

    public static final boolean IS_CAN_BE_USED = true;
    public static final boolean CAN_NOT_BE_USED = false;

    public PictureFormatDao(){
        super(PictureFormat.class);
    }

    public List<PictureFormat> get(PictureFormat pictureFormat){
        return DbAssistant.getSessionFactory()
                .getCurrentSession()
                .createQuery("from " + PictureFormat.class.getSimpleName()
                        + " where " + WIDTH + " = :value_width"
                        + " and " + HEIGHT + " = :value_height"
                        , PictureFormat.class)
                .setParameter("value_width", pictureFormat.getWidth())
                .setParameter("value_height", pictureFormat.getHeight())
                .list();
    }

    public List<PictureFormat> getCanBeUsedFormats(){
        return getFormatsByCondition(IS_CAN_BE_USED);
    }

    public List<PictureFormat> getMarkedForDeleteFormats(){
        return getFormatsByCondition(CAN_NOT_BE_USED);
    }

    private List<PictureFormat> getFormatsByCondition(boolean canBeUsed){
        return DbAssistant.getSessionFactory()
                .getCurrentSession()
                .createQuery("from " + PictureFormat.class
                        + " where " + CAN_BE_USED + " = :value_can_be_used", PictureFormat.class)
                .setParameter("value_can_be_used", canBeUsed)
                .list();
    }
}
