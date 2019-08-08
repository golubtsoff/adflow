package service;

import dao.DaoFactory;
import dao.DbAssistant;
import entity.users.Status;
import entity.users.customer.Campaign;
import entity.users.customer.Customer;
import entity.users.customer.Picture;
import entity.users.PictureFormat;
import exception.ConflictException;
import exception.DbException;
import exception.NotFoundException;
import exception.ServiceException;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import util.NullAware;

import javax.persistence.NoResultException;
import javax.validation.constraints.NotNull;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;

public class CampaignService {

    public static Campaign create(long userId, @NotNull Object campaignDto)
            throws NotFoundException, DbException, ServiceException, ConflictException {
        Transaction transaction = DbAssistant.getTransaction();
        Campaign campaign = null;
        try {
            Customer customer = DaoFactory.getCustomerDao().getByUserId(userId);
            if (customer == null){
                DbAssistant.transactionRollback(transaction);
                throw new NotFoundException("Customer with user's id=" + String.valueOf(userId) + " not found");
            }
            Hibernate.initialize(customer.getCampaigns());

            if (campaignDto instanceof rest.customer.CampaignResource.CampaignDto) {
                rest.customer.CampaignResource.CampaignDto campaignDtoCast
                        = (rest.customer.CampaignResource.CampaignDto) campaignDto;

                checkExistedTitle(customer.getCampaigns(), campaignDtoCast.getTitle());
                checkAndPersistPictureFormats(campaignDtoCast);
            }

            campaign = new Campaign(customer);
            NullAware.getInstance().copyProperties(campaign, campaignDto);
            DaoFactory.getCampaignDao().create(campaign);
            transaction.commit();
            return campaign;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            DbAssistant.transactionRollback(transaction);
            throw new ServiceException("Error copy objects: "
                    + campaignDto.toString() + " to " + campaign.toString(), e);
        }
    }

    private static void checkAndPersistPictureFormats(rest.customer.CampaignResource.CampaignDto campaignDto)
            throws NotFoundException {
        Set<Picture> pictures = campaignDto.getPictures();
        if (pictures == null) return;

        List<PictureFormat> canBeUsedFormats = DaoFactory.getPictureFormatDao().getCanBeUsedFormats();

        for (Picture picture : pictures) {
            boolean formatIsExist = false;
            for (PictureFormat pictureFormat : canBeUsedFormats) {
                if (picture.getPictureFormat().equals(pictureFormat)) {
                    picture.setPictureFormat(pictureFormat);
                    formatIsExist = true;
                    break;
                }
            }
            if (!formatIsExist) {
                throw new NotFoundException("Unknown picture's format: " + picture.getPictureFormat());
            }
        }
    }

    private static void checkExistedTitle(Set<Campaign> campaigns, String title) throws ConflictException {
        for (Campaign campaign : campaigns){
            if (campaign.getTitle().equals(title)){
                throw new ConflictException("Title '" + title + "' is already used");
            }
        }
    }

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
            Campaign campaign = checkAndGetCampaign(campaignId, userId);
            Hibernate.initialize(campaign.getPictures());
            Hibernate.initialize(campaign.getCustomer());
            transaction.commit();
            return campaign;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        } catch (NotFoundException e){
            DbAssistant.transactionRollback(transaction);
            throw new NotFoundException(e);
        }
    }

    private static Campaign checkAndGetCampaign(long campaignId, long userId)
            throws NotFoundException {
        Campaign campaign = DaoFactory.getCampaignDao().get(campaignId);
        Customer customer = DaoFactory.getCustomerDao().getByUserId(userId);
        if (campaign == null
                || customer == null
                || !campaign.getCustomer().getId().equals(customer.getId())){
            throw new NotFoundException();
        }
        return campaign;
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
            long userId, long campaignId, @NotNull Object campaignDto)
            throws DbException, NotFoundException, ServiceException, ConflictException {
        Transaction transaction = DbAssistant.getTransaction();
        Campaign campaign = null;
        try {
            campaign = checkAndGetCampaign(campaignId, userId);

            if (campaignDto instanceof rest.customer.CampaignResource.CampaignDto) {
                rest.customer.CampaignResource.CampaignDto campaignDtoCast
                        = (rest.customer.CampaignResource.CampaignDto) campaignDto;

                if (!campaign.getTitle().equals(campaignDtoCast.getTitle())){
                    checkExistedTitle(campaign.getCustomer().getCampaigns(), campaignDtoCast.getTitle());
                }

                checkAndPersistPictureFormats(campaignDtoCast);
            }

            NullAware.getInstance().copyProperties(campaign, campaignDto);
            DaoFactory.getCampaignDao().update(campaign);

            transaction.commit();
            return campaign;
        }catch (NotFoundException e){
            DbAssistant.transactionRollback(transaction);
            throw new NotFoundException(e);
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            DbAssistant.transactionRollback(transaction);
            throw new ServiceException("Error copy objects: "
                    + campaignDto.toString() + " to " + campaign.toString(), e);
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
        try {
            checkAndGetCampaign(campaignId, userId);
            DaoFactory.getCampaignDao().delete(campaignId);
            transaction.commit();
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        } catch (NotFoundException e){
            DbAssistant.transactionRollback(transaction);
            throw new NotFoundException(e);
        }
    }

    public static Campaign setStatusRemovedWithChecking(long userId, long campaignId)
            throws NotFoundException, DbException {
        Transaction transaction = DbAssistant.getTransaction();
        Campaign campaignFromBase;
        try {
            campaignFromBase = checkAndGetCampaign(campaignId, userId);

            campaignFromBase.setStatus(Status.REMOVED);
            DaoFactory.getCampaignDao().update(campaignFromBase);
            transaction.commit();
            return campaignFromBase;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        } catch (NotFoundException e){
            DbAssistant.transactionRollback(transaction);
            throw new NotFoundException(e);
        }
    }
}
