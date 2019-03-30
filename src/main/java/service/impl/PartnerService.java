package service.impl;

import entity.users.partner.Partner;
import service.AbstractConcreteRoleService;

@Deprecated
public class PartnerService extends AbstractConcreteRoleService<Partner> {

    public PartnerService(){
        super(Partner.class);
    }
}
