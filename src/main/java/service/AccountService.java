package service;

import dao.DaoFactory;
import dao.DbAssistant;
import entity.users.Account;
import entity.users.customer.Customer;
import entity.users.partner.Partner;
import entity.users.user.Role;
import entity.users.user.User;
import exception.DbException;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;

import javax.persistence.NoResultException;
import javax.validation.constraints.NotNull;

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
}
