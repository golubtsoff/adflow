package service;

import entity.users.ConcreteRole;
import exception.DbException;
import exception.ServiceException;

import java.lang.reflect.InvocationTargetException;

@Deprecated
public interface ConcreteRoleService<T extends ConcreteRole> {
    T signIn(String login, String password) throws DbException, ServiceException;
    T signUp(String login, String password) throws DbException, ServiceException;
}
