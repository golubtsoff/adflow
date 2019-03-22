package dao.impl;

import dao.AbstractDao;
import entity.users.Administrator;
import entity.users.customer.Campaign;

public class AdministratorDao extends AbstractDao<Administrator> {

    public AdministratorDao(){
        super(Administrator.class);
    }
}
