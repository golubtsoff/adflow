package service;

import dao.DaoFactory;
import dao.DbAssistant;
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

public abstract class UserService {

    public static User signIn(String login, String password) throws DbException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            List<User> users = DaoFactory.getUserDao().getByName(login);
            if (users.isEmpty()
                    || !users.get(0).getHash().equalsIgnoreCase(Hash.getHash(password))){
                DbAssistant.transactionRollback(transaction);
                return null;
            }

            transaction.commit();
            return users.get(0);
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    public static User signUp(String login, String password, String roleString)
            throws DbException, ServiceException {
        try {
            Role role = Role.valueOf(roleString.toUpperCase());
            if (role == Role.ADMIN) return null;
            return signUp(login, password, role);
        } catch (IllegalArgumentException e){
            throw new ServiceException(e);
        }
    }

    private static User signUp(String login, String password, Role role)
            throws DbException, ServiceException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            if (isExist(login)){
                DbAssistant.transactionRollback(transaction);
                return null;
            }
            User user = new User(login, Hash.getHash(password), role);
            DaoFactory.getUserDao().create(user);
            saveConcreteRole(user);

            transaction.commit();
            return user;
        } catch (IllegalArgumentException e){
            throw new ServiceException(e);
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    private static boolean isExist(String login) {
        List<User> users = DaoFactory.getUserDao().getByName(login);
        return !users.isEmpty();
    }

    private static void saveConcreteRole(User user){
        if (user.getRole() == Role.ADMIN){
            Administrator administrator = new Administrator(user);
            DaoFactory.getAdministratorDao().create(administrator);
        } else if (user.getRole() == Role.PARTNER){
            Partner partner = new Partner(user);
            DaoFactory.getPartnerDao().create(partner);
        } else {
            Customer customer = new Customer(user);
            DaoFactory.getCustomerDao().create(customer);
        }
    }

}