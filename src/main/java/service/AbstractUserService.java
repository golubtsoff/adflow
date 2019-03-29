package service;

import dao.Dao;
import dao.DaoFactory;
import dao.DbAssistant;
import entity.users.Administrator;
import entity.users.partner.Partner;
import entity.users.user.Role;
import entity.users.user.User;
import exception.DbException;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import util.Hash;

import javax.persistence.NoResultException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public abstract class AbstractUserService<T> implements UserService<T> {

    private Class<T> parameterizedClass;

    protected AbstractUserService(Class<T> cl){
        this.parameterizedClass = cl;
    }

    @Override
    public T signIn(String login, String password) throws DbException {
        return null;
    }

    @Override
    public T signUp(String login, String password) throws DbException{
        Role role = getRole(parameterizedClass);

        Transaction transaction = DbAssistant.getTransaction();
        try {
            if (isExist(login)){
                DbAssistant.transactionRollback(transaction);
                return null;
            }
            User user = new User(login, Hash.getHash(password), role);
            DaoFactory.getUserDao().create(user);

            Object object = parameterizedClass.getDeclaredConstructor(User.class).newInstance(user);
            Dao<T> dao = DaoFactory.getDaoByParametrizedClass(parameterizedClass);
            assert dao != null;
            dao.create(parameterizedClass.cast(object));

            transaction.commit();
            return parameterizedClass.cast(object);
        } catch (HibernateException | NoResultException | NullPointerException | IllegalAccessException
                | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    private Role getRole(Class cl){
        if (cl == Administrator.class)
            return Role.ADMIN;
        else if (cl == Partner.class)
            return Role.PARTNER;
        else
            return Role.CUSTOMER;
    }

    protected boolean isExist(String login) {
            List<User> users = DaoFactory.getUserDao().getByName(login);
            return !users.isEmpty();
    }
}
