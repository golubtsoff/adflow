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

@Deprecated
public abstract class AbstractConcreteRoleService<T extends ConcreteRole> implements ConcreteRoleService<T> {

    private Class<T> parameterizedClass;

    protected AbstractConcreteRoleService(Class<T> cl){
        this.parameterizedClass = cl;
    }

    @Override
    public T signIn(String login, String password) throws DbException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            List<User> users = DaoFactory.getUserDao().getByName(login);
            if (users.isEmpty()){
                DbAssistant.transactionRollback(transaction);
                return null;
            }

            User user = users.get(0);
            if (!user.getHash().equalsIgnoreCase(Hash.getHash(password))){
                DbAssistant.transactionRollback(transaction);
                return null;
            }

            Object object;
            if (user.getRole() == Role.ADMIN){
                object = DaoFactory.getAdministratorDao().getByUserId(user.getId());
            } else if (user.getRole() == Role.PARTNER){
                object = DaoFactory.getPartnerDao().getByUserId(user.getId());
            } else {
                object = DaoFactory.getCustomerDao().getByUserId(user.getId());
            }

            transaction.commit();
            return parameterizedClass.cast(object);
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    @Override
    public T signUp(String login, String password) throws DbException, ServiceException {
        Role role = Role.getRole(parameterizedClass);

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
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            DbAssistant.transactionRollback(transaction);
            throw new ServiceException(e);
        }
    }

    protected boolean isExist(String login) {
            List<User> users = DaoFactory.getUserDao().getByName(login);
            return !users.isEmpty();
    }
}
