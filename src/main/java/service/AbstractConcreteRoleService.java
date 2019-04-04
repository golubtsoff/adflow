package service;

import dao.Dao;
import dao.DaoFactory;
import dao.DbAssistant;
import entity.users.ConcreteRole;
import entity.users.user.Role;
import entity.users.user.User;
import exception.DbException;
import exception.ServiceException;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import util.Hash;

import javax.persistence.NoResultException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public abstract class AbstractConcreteRoleService<T extends ConcreteRole> implements ConcreteRoleService<T> {

    private Class<T> parameterizedClass;

    protected AbstractConcreteRoleService(Class<T> cl){
        this.parameterizedClass = cl;
    }

    public Class<T> getParameterizedClass() {
        return parameterizedClass;
    }

}
