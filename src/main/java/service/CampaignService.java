package service;

import dao.DaoFactory;
import dao.DbAssistant;
import entity.users.customer.Campaign;
import entity.users.customer.Customer;
import entity.users.user.User;
import exception.DbException;
import exception.NotFoundException;
import exception.ServiceException;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import util.NullAware;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class CampaignService {

    @NotNull
    public static List<Campaign> getAllByUserId(long userId) throws DbException, NotFoundException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            Customer customer = DaoFactory.getCustomerDao().getByUserId(userId);
            if (customer == null){
                DbAssistant.transactionRollback(transaction);
                throw new NotFoundException("Customer with userId=" + String.valueOf(userId) + " not found");
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

    public static Campaign get(long id) throws DbException, NotFoundException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            Campaign campaign = DaoFactory.getCampaignDao().get(id);
            if (campaign == null) {
                DbAssistant.transactionRollback(transaction);
                throw new NotFoundException("Campaign with id=" + String.valueOf(id) + " not found");
            }
            transaction.commit();
            return campaign;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    public static Campaign getWithChecking(long userId, long campaignId) throws DbException, NotFoundException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            Campaign campaign = DaoFactory.getCampaignDao().get(campaignId);
            Customer customer = DaoFactory.getCustomerDao().getByUserId(userId);
            checkCampaign(campaign, customer, transaction);

            transaction.commit();
            return campaign;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    private static void checkCampaign(Campaign campaign, Customer customer, Transaction transaction)
            throws NotFoundException {
        if (campaign == null
                || customer == null
                || !campaign.getCustomer().getId().equals(customer.getId())){
            DbAssistant.transactionRollback(transaction);
            throw new NotFoundException();
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

    public static Campaign updateExcludeNullWithChecking(
            long userId, long campaignId, @NotNull Object campaignFromClient
    )
            throws DbException, NotFoundException, ServiceException {
        Transaction transaction = DbAssistant.getTransaction();
        Campaign campaignFromBase = null;
        try {
            campaignFromBase = DaoFactory.getCampaignDao().get(campaignId);
            Customer customer = DaoFactory.getCustomerDao().getByUserId(userId);
            checkCampaign(campaignFromBase, customer, transaction);

            NullAware.getInstance().copyProperties(campaignFromBase, campaignFromClient);
            DaoFactory.getCampaignDao().update(campaignFromBase);

            transaction.commit();
            return campaignFromBase;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            DbAssistant.transactionRollback(transaction);
            throw new ServiceException("Error copy objects: "
                    + campaignFromClient.toString() + " to " + campaignFromBase.toString(), e);
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

    public static void deleteWithChecking(long userId, long campaignId) throws DbException, NotFoundException {
        Transaction transaction = DbAssistant.getTransaction();
        Campaign campaignFromBase;
        try {
            campaignFromBase = DaoFactory.getCampaignDao().get(campaignId);
            Customer customer = DaoFactory.getCustomerDao().getByUserId(userId);
            checkCampaign(campaignFromBase, customer, transaction);

            DaoFactory.getCampaignDao().delete(campaignId);
            transaction.commit();
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }
}
