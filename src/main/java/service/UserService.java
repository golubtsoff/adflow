package service;

import entity.users.user.Role;
import entity.users.user.User;
import exception.DBException;

public interface UserService<T> {
    T signIn(String name, String password) throws DBException;
    T signUp(String name, String password) throws DBException;
    T signUp(String name, String password, Role role) throws DBException;
    boolean isExist(String name) throws DBException;
}
