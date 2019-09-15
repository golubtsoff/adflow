package dao.impl;

import dao.AbstractDao;
import dao.DbAssistant;
import entity.users.Status;
import entity.users.user.Role;
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

    public List<User> getByStatus(Status status){
        return getAll("status", status);
    }

    public List<User> getByRole(Role role){
        return getAll("role", role);
    }
}
