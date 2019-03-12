package dao;

import entity.user.User;

public class UserDao extends DaoImpl<User> {

    public UserDao(){
        super(User.class);
    }
}
