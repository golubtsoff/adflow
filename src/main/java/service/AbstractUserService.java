package service;

import dao.DaoFactory;
import dao.DbAssistant;
import dao.impl.AdministratorDao;
import dao.impl.CustomerDao;
import dao.impl.PartnerDao;
import dao.impl.UserDao;
import entity.users.Administrator;
import entity.users.customer.Customer;
import entity.users.partner.Partner;
import entity.users.user.Role;
import entity.users.user.User;
import exception.DbException;
import exception.ServiceException;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import util.Hash;

import javax.persistence.NoResultException;
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
    public T signUp(String login, String password) throws DbException {
        return null;
    }

    @Override
    public T signUp(String login, String password, Role role) throws DbException, ServiceException {
        if (isExist(login)) return null;

        Transaction transaction = DbAssistant.getTransaction();
        try {
            User user = new User(login, Hash.getHash(password), role);
            UserDao userDao = DaoFactory.getUserDao();
            Long idUser = userDao.create(user);

            if (role == Role.PARTNER){

                Partner partner = new Partner(user);
                PartnerDao partnerDao = DaoFactory.getPartnerDao();
                Long idPartner = partnerDao.create(partner);

                transaction.commit();
                return parameterizedClass.cast(partner);

            } else if (role == Role.CUSTOMER){

                Customer customer = new Customer(user);
                CustomerDao customerDao = DaoFactory.getCustomerDao();
                Long idCustomer = customerDao.create(customer);

                transaction.commit();
                return parameterizedClass.cast(customer);

            } else if (role == Role.ADMIN){

                Administrator administrator = new Administrator(user);
                AdministratorDao administratorDao = DaoFactory.getAdministratorDao();
                Long idAdministrator = administratorDao.create(administrator);

                transaction.commit();
                return parameterizedClass.cast(administrator);

            } else {
                throw new ServiceException("Unknown user role: " + role.toString());
            }
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    @Override
    public boolean isExist(String login) throws DbException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            List<User> users = DaoFactory.getUserDao().getByName(login);
            transaction.commit();
            return !users.isEmpty();
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }
}
