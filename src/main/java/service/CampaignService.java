package service;

import dao.DaoFactory;
import dao.DbAssistant;
import entity.users.customer.Campaign;
import entity.users.customer.Customer;
import entity.users.user.User;
import exception.DbException;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;

import javax.persistence.NoResultException;
import javax.validation.constraints.NotNull;
import java.util.List;

public class CampaignService {

    public static List<Campaign> getAllByUserId(long userId) throws DbException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            Customer customer = DaoFactory.getCustomerDao().getByUserId(userId);
            if (customer == null) {
                DbAssistant.transactionRollback(transaction);
                return null;
            }
            List<Campaign> campaigns = DaoFactory.getCampaignDao().getAllByCustomerId(customer.getId());
            transaction.commit();
            return campaigns;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    public static List<Campaign> getAll() throws DbException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            List<Campaign> campaigns = DaoFactory.getCampaignDao().getAll();
            transaction.commit();
            return campaigns;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    public static Campaign get(long id) throws DbException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            Campaign campaign = DaoFactory.getCampaignDao().get(id);
            if (campaign == null) {
                DbAssistant.transactionRollback(transaction);
                return null;
            }
            transaction.commit();
            return campaign;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    public static Campaign getWithChecking(long userId, long campaignId) throws DbException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            Campaign campaign = DaoFactory.getCampaignDao().get(campaignId);
            Customer customer = DaoFactory.getCustomerDao().getByUserId(userId);
            if (campaign == null
                    || customer == null
                    || !campaign.getCustomer().getId().equals(customer.getId())){
                DbAssistant.transactionRollback(transaction);
                return null;
            }
            transaction.commit();
            return campaign;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    public static void update(Campaign campaign) throws DbException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            DaoFactory.getCampaignDao().update(campaign);
            transaction.commit();
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    public static void updateWithChecking(long userId, @NotNull Campaign campaign) throws DbException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            Campaign campaignFromBase = DaoFactory.getCampaignDao().get(campaign.getId());
            Customer customer = DaoFactory.getCustomerDao().getByUserId(userId);
            if (campaignFromBase == null
                    || customer == null
                    || !campaignFromBase.getCustomer().getId().equals(customer.getId())){
                DbAssistant.transactionRollback(transaction);
            }
            DaoFactory.getCampaignDao().update(campaign);
            transaction.commit();
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    public static void delete(long id) throws DbException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            DaoFactory.getCampaignDao().delete(id);
            transaction.commit();
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }
}
