package dao.impl;

import dao.AbstractDao;
import entity.users.Administrator;
import entity.users.customer.Campaign;

import java.util.List;

public class AdministratorDao extends AbstractDao<Administrator> {

    public AdministratorDao(){
        super(Administrator.class);
    }

    public Administrator getByUserId(Long userId) {
        return get("USER_ID", userId);
    }
}
