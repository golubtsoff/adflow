package service;

import entity.users.ConcreteRole;
import exception.DbException;
import exception.ServiceException;

import java.lang.reflect.InvocationTargetException;

public interface UserService<T extends ConcreteRole> {
    T signIn(String login, String password) throws DbException;
    T signUp(String login, String password) throws DbException, ServiceException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException;
}
