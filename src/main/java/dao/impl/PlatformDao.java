package dao.impl;

import dao.AbstractDao;
import entity.users.partner.Platform;

import java.util.List;

public class PlatformDao extends AbstractDao<Platform> {

    public PlatformDao(){
        super(Platform.class);
    }

    public List<Platform> getAllByPartnerId(Long partnerId) {
        return getAll(Platform.PARTNER_ID, partnerId.toString());
    }

    public List<Platform> getAllByPictureFormatId(Long pictureFormatId) {
        return getAll(Platform.PICTURE_FORMAT_ID, pictureFormatId.toString());
    }
}
