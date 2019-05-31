package service;

import dao.Dao;
import dao.DaoFactory;
import dao.DbAssistant;
import dao.impl.CustomerDao;
import dao.impl.PartnerDao;
import entity.users.Account;
import entity.users.customer.Customer;
import entity.users.partner.Partner;
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
    public static Account get(long userId) throws DbException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            User user = DaoFactory.getUserDao().get(userId);

            Account account;
            if (user.getRole() == Role.PARTNER){
                Partner partner = DaoFactory.getPartnerDao().getByUserId(userId);
                account = partner.getAccount();
            } else if (user.getRole() == Role.CUSTOMER){
                Customer customer = DaoFactory.getCustomerDao().getByUserId(userId);
                account = customer.getAccount();
            } else {
                throw new NoResultException();
            }
            transaction.commit();
            return account;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    public static void update(long userId, @NotNull Account account) throws DbException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            User user = DaoFactory.getUserDao().get(userId);

            if (user.getRole() == Role.PARTNER){
                Partner partner = DaoFactory.getPartnerDao().getByUserId(userId);
                partner.setAccount(account);
                DaoFactory.getPartnerDao().update(partner);
            } else if (user.getRole() == Role.CUSTOMER){
                Customer customer = DaoFactory.getCustomerDao().getByUserId(userId);
                customer.setAccount(account);
                DaoFactory.getCustomerDao().update(customer);
            } else {
                throw new NoResultException();
            }
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
            if (userFromBase == null){
                DbAssistant.transactionRollback(transaction);
                throw new NotFoundException("User with id=" + String.valueOf(userId) + " not found");
            }

            if (userFromBase.getRole() == Role.PARTNER){
                PartnerDao dao = DaoFactory.getPartnerDao();
                Partner partner = dao.getByUserId(userId);
                accountFromBase = partner.getAccount();
                NullAware.getInstance().copyProperties(accountFromBase, accountFromClient);
                partner.setAccount(accountFromBase);
                dao.update(partner);
            } else if (userFromBase.getRole() == Role.CUSTOMER){
                CustomerDao dao = DaoFactory.getCustomerDao();
                Customer customer = dao.getByUserId(userId);
                accountFromBase = customer.getAccount();
                NullAware.getInstance().copyProperties(accountFromBase, accountFromClient);
                customer.setAccount(accountFromBase);
                dao.update(customer);
            } else {
                throw new NotFoundException();
            }
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
