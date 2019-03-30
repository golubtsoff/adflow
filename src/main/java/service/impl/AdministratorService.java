package service.impl;

import entity.users.Administrator;
import service.AbstractConcreteRoleService;

@Deprecated
public class AdministratorService extends AbstractConcreteRoleService<Administrator> {

    public AdministratorService(){
        super(Administrator.class);
    }
}
