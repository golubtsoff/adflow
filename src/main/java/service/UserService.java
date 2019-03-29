package service;

import entity.users.ConcreteUser;
import entity.users.user.Role;
import exception.DbException;
import exception.ServiceException;

import java.lang.reflect.InvocationTargetException;

public interface UserService<T extends ConcreteUser> {
    T signIn(String login, String password) throws DbException;
    T signUp(String login, String password) throws DbException, ServiceException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException;
}
