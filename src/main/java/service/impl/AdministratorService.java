package service.impl;

import entity.users.Administrator;
import service.AbstractUserService;

public class AdministratorService extends AbstractUserService<Administrator> {

    public AdministratorService(){
        super(Administrator.class);
    }
}
