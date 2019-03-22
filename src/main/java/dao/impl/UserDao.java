package dao.impl;

import dao.AbstractDao;
import entity.users.user.User;

public class UserDao extends AbstractDao<User> {

    public UserDao(){
        super(User.class);
    }
}
