package dao;

import entity.users.user.User;

public class UserDao extends DaoImpl<User> {

    public UserDao(){
        super(User.class);
    }
}
