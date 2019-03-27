package service;

import entity.users.user.Role;
import entity.users.user.User;
import exception.DBException;

public abstract class AbstractUserService<T> implements UserService<T> {

    private Class<T> parameterizedClass;

    protected AbstractUserService(Class<T> cl){
        this.parameterizedClass = cl;
    }

    @Override
    public T signIn(String name, String password) throws DBException {
        return null;
    }

    @Override
    public T signUp(String name, String password) throws DBException {
        return null;
    }

    @Override
    public T signUp(String name, String password, Role role) throws DBException {
        return null;
    }

    @Override
    public boolean isExist(String name) throws DBException {
        return false;
    }
}
