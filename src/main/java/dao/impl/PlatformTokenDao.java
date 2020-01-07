package dao.impl;

import dao.AbstractDao;
import entity.users.partner.PlatformToken;

public class PlatformTokenDao extends AbstractDao<PlatformToken> {

    public PlatformTokenDao(){
        super(PlatformToken.class);
    }
}
