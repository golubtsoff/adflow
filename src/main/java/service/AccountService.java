package service;

import dao.CustomerPartnerDao;
import dao.DaoFactory;
import dao.DbAssistant;
import entity.users.Account;
import entity.users.Accountable;
import entity.users.user.Role;
import entity.users.user.User;
import exception.DbException;
import exception.NotFoundException;
import exception.ServiceException;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import util.NullAware;

import javax.persistence.NoResultException;
import javax.validation.constraints.NotNull;
import java.lang.reflect.InvocationTargetException;

public class AccountService {

    @NotNull
    public static Account get(long userId) throws DbException, NotFoundException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            User user = DaoFactory.getUserDao().get(userId);
            checkUserNotNull(userId,  user, transaction);

            Accountable accountable;
            if (user.getRole() == Role.PARTNER){
                accountable = DaoFactory.getPartnerDao().getByUserId(userId);
            } else if (user.getRole() == Role.CUSTOMER){
                accountable = DaoFactory.getCustomerDao().getByUserId(userId);
            } else {
                throw new NoResultException();
            }
            Account account = accountable.getAccount();
            transaction.commit();
            return account;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    private static void checkUserNotNull(long userId, User user, Transaction transaction)
            throws NotFoundException {
        if (user == null){
            DbAssistant.transactionRollback(transaction);
            throw new NotFoundException("User with id=" + String.valueOf(userId) + " not found");
        }
    }

    public static void update(long userId, @NotNull Account account) throws DbException, NotFoundException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            User user = DaoFactory.getUserDao().get(userId);
            checkUserNotNull(userId,  user, transaction);

            CustomerPartnerDao dao;
            if (user.getRole() == Role.PARTNER){
                dao = DaoFactory.getPartnerDao();
            } else if (user.getRole() == Role.CUSTOMER){
                dao = DaoFactory.getCustomerDao();
            } else {
                throw new NoResultException();
            }

            Accountable accountable = (Accountable)dao.getByUserId(userId);
            accountable.setAccount(account);
            dao.update(accountable);

            transaction.commit();
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    public static Account updateExcludeNull(long userId, @NotNull Account accountFromClient)
            throws DbException, NotFoundException, ServiceException {
        Transaction transaction = DbAssistant.getTransaction();
        Account accountFromBase = null;
        try {
            User userFromBase = DaoFactory.getUserDao().get(userId);
            checkUserNotNull(userId,  userFromBase, transaction);

            CustomerPartnerDao dao;
            Accountable accountable;
            if (userFromBase.getRole() == Role.PARTNER){
                dao = DaoFactory.getPartnerDao();
            } else if (userFromBase.getRole() == Role.CUSTOMER){
                dao = DaoFactory.getCustomerDao();
            } else {
                throw new NotFoundException();
            }

            accountable = (Accountable)dao.getByUserId(userId);
            accountFromBase = accountable.getAccount();
            NullAware.getInstance().copyProperties(accountFromBase, accountFromClient);
            accountable.setAccount(accountFromBase);
            dao.update(accountable);

            transaction.commit();
            return accountFromBase;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            DbAssistant.transactionRollback(transaction);
            throw new ServiceException("Error copy objects: "
                    + accountFromClient.toString() + " to " + accountFromBase.toString(), e);
        }
    }
}
