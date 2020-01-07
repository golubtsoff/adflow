package service;

import dao.DaoFactory;
import dao.DbAssistant;
import entity.users.AdvertisingEntity;
import entity.users.Action;
import entity.users.Administrator;
import entity.users.Status;
import entity.users.customer.Campaign;
import entity.users.customer.Customer;
import entity.users.partner.Partner;
import entity.users.partner.Platform;
import entity.users.user.Role;
import entity.users.user.User;
import entity.users.user.UserToken;
import exception.DbException;
import exception.NotFoundException;
import exception.ServiceException;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import util.Hash;
import util.Links;
import util.NullAware;

import javax.persistence.NoResultException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;

public abstract class UserService {

    public static UserToken signIn(String login, String password) throws DbException, ServiceException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            List<User> users = DaoFactory.getUserDao().getByName(login);
            if (users.isEmpty()
                    || users.get(0).getStatus() != Status.WORKING
                    || !users.get(0).getPasswordHash().equalsIgnoreCase(Hash.getHash(password))){
                DbAssistant.transactionRollback(transaction);
                return null;
            }
            UserToken userToken = DaoFactory.getUserTokenDao().get(users.get(0).getId());
            if (userToken == null){
                userToken = new UserToken(users.get(0));
                DaoFactory.getUserTokenDao().create(userToken);
            } else {
                userToken.updateToken();
                DaoFactory.getUserTokenDao().update(userToken);
            }

            transaction.commit();
            return userToken;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        } catch (Exception e){
            DbAssistant.transactionRollback(transaction);
            throw new ServiceException(e);
        }
    }

    public static User signUpExceptAdministrator(String login, String password, String roleString)
            throws DbException, ServiceException {
        try {
            Role role = Role.valueOf(roleString.toUpperCase());
            if (role == Role.ADMIN) return null;
            return signUp(login, password, role);
        } catch (IllegalArgumentException e){
            throw new ServiceException(e);
        }
    }

    public static User signUp(String login, String password, Role role)
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

    public static void signOut(Long userId) throws DbException{
        Transaction transaction = DbAssistant.getTransaction();
        try {
            UserToken token = DaoFactory.getUserTokenDao().get(userId);
            token.setExpired();
            transaction.commit();
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    public static UserToken getToken(Long userId) throws DbException{
        Transaction transaction = DbAssistant.getTransaction();
        try {
            UserToken token = DaoFactory.getUserTokenDao().get(userId);
            transaction.commit();
            return token;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    public static void setToken(UserToken token) throws DbException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            DaoFactory.getUserTokenDao().update(token);
            transaction.commit();
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    public static User get(long id) throws DbException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            User user = DaoFactory.getUserDao().get(id);
            transaction.commit();
            return user;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    public static User get(String login) throws DbException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            User user = DaoFactory.getUserDao().getByName(login).get(0);
            transaction.commit();
            return user;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    public static List<User> getAll() throws DbException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            List<User> users = DaoFactory.getUserDao().getAll();
            transaction.commit();
            return users;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    public static List<User> getByStatus(Status status) throws DbException {
        return getByCustomField(User.STATUS, status);
    }

    public static List<User> getByRole(Role role) throws DbException {
        return getByCustomField(User.ROLE, role);
    }

    private static <U> List<User> getByCustomField(String fieldName, U value) throws DbException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            List<User> users = DaoFactory.getUserDao().getAll(fieldName, value);
            transaction.commit();
            return users;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    public static User update(User user) throws DbException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            updateAction(user);
            DaoFactory.getUserDao().merge(user);
            transaction.commit();
            return user;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    private static void updateAction(User user){
        if (user.getRole() == Role.CUSTOMER){
            Customer customer = DaoFactory.getCustomerDao().getByUserId(user.getId());
            Set<Campaign> campaigns = customer.getCampaigns();
            for (Campaign campaign : campaigns) {
                setAction(user.getStatus(), campaign);
            }
            DaoFactory.getCustomerDao().update(customer);

        } else if (user.getRole() == Role.PARTNER){
            Partner partner = DaoFactory.getPartnerDao().getByUserId(user.getId());
            Set<Platform> platforms = partner.getPlatforms();
            for (Platform platform : platforms) {
                setAction(user.getStatus(), platform);
            }
            DaoFactory.getPartnerDao().update(partner);
        }
    }

    private static void setAction(Status userStatus, AdvertisingEntity element){
        if (userStatus == Status.REMOVED){
            element.setAction(Action.STOP);
        } else if (userStatus == Status.LOCKED
                || userStatus == Status.CHECKING){
            if (element.getAction() == Action.RUN){
                element.setAction(Action.PAUSE);
            }
        }
    }

    public static User updateExcludeNull(long userId, Object userFromClient)
            throws DbException, NotFoundException, ServiceException {
        Transaction transaction = DbAssistant.getTransaction();
        User userFromBase = null;
        try {
            userFromBase = get(userId);
            if (userFromBase == null){
                DbAssistant.transactionRollback(transaction);
                throw new NotFoundException("User with id=" + userId + " not found");
            }
            NullAware.getInstance().copyProperties(userFromBase, userFromClient);
            updateAction(userFromBase);
            DaoFactory.getUserDao().update(userFromBase);

            transaction.commit();
            return userFromBase;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            DbAssistant.transactionRollback(transaction);
            throw new ServiceException("Error copy objects: "
                    + userFromClient.toString() + " to " + userFromBase.toString(), e);
        }
    }

    public static void delete(long id) throws DbException, IOException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            Customer customer = DaoFactory.getCustomerDao().getByUserId(id);
            Links.deleteFolder(customer.getId());
            DaoFactory.getUserDao().delete(id);
            transaction.commit();
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        } catch (IOException e) {
            DbAssistant.transactionRollback(transaction);
            throw e;
        }
    }

}