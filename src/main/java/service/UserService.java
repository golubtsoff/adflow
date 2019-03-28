package service;

import entity.users.user.Role;
import exception.DbException;
import exception.ServiceException;

public interface UserService<T> {
    T signIn(String login, String password) throws DbException;
    T signUp(String login, String password) throws DbException;
    T signUp(String login, String password, Role role) throws DbException, ServiceException;
    boolean isExist(String login) throws DbException;
}
