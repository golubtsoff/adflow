package service.impl;

import entity.users.partner.Partner;
import service.AbstractUserService;

public class PartnerService extends AbstractUserService<Partner> {

    public PartnerService(){
        super(Partner.class);
    }
}
