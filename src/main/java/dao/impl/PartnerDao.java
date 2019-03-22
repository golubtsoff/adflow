package dao.impl;

import dao.AbstractDao;
import entity.users.partner.Partner;

public class PartnerDao extends AbstractDao<Partner> {

    public PartnerDao(){
        super(Partner.class);
    }
}
