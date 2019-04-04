package service.impl;

import entity.users.Administrator;
import entity.users.user.User;
import service.AbstractConcreteRoleService;

import java.util.List;

public class AdministratorService extends AbstractConcreteRoleService<Administrator> {

    public AdministratorService(){
        super(Administrator.class);
    }


}
