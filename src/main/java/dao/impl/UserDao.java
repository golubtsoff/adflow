package dao.impl;

import dao.AbstractDao;
import dao.DbAssistant;
import entity.users.user.User;
import org.hibernate.LockMode;

import java.util.List;

public class UserDao extends AbstractDao<User> {

    public UserDao(){
        super(User.class);
    }

    public List<User> getByName(String login) {
        return getAll("login", login);
    }
}
