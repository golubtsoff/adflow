package dao.impl;

import dao.AbstractDao;
import dao.CustomerPartnerDao;
import entity.users.partner.Partner;

public class PartnerDao extends AbstractDao<Partner> implements CustomerPartnerDao<Partner> {

    public PartnerDao(){
        super(Partner.class);
    }

    public Partner getByUserId(Long userId) {
        return get("USER_ID", userId.toString());
    }
}
