package service;

import dao.DaoFactory;
import dao.DbAssistant;
import entity.users.customer.Campaign;
import entity.users.customer.Customer;
import entity.users.partner.Partner;
import entity.users.partner.Platform;
import exception.DbException;
import exception.NotFoundException;
import exception.ServiceException;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import util.NullAware;

import javax.persistence.NoResultException;
import javax.validation.constraints.NotNull;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class PlatformService {

    @NotNull
    public static List<Platform> getAllByUserId(long userId) throws DbException, NotFoundException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            Partner partner = DaoFactory.getPartnerDao().getByUserId(userId);
            if (partner == null){
                DbAssistant.transactionRollback(transaction);
                throw new NotFoundException("Partner with userId=" + String.valueOf(userId) + " not found");
            }

            List<Platform> platforms = DaoFactory.getPlatformDao().getAllByPartnerId(partner.getId());
            transaction.commit();
            return platforms;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    public static List<Platform> getAll() throws DbException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            List<Platform> platforms = DaoFactory.getPlatformDao().getAll();
            transaction.commit();
            return platforms;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    public static Platform get(long id) throws DbException, NotFoundException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            Platform platform = DaoFactory.getPlatformDao().get(id);
            if (platform == null) {
                DbAssistant.transactionRollback(transaction);
                throw new NotFoundException("Platform with id=" + String.valueOf(id) + " not found");
            }
            transaction.commit();
            return platform;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    public static Platform getWithChecking(long userId, long platformId) throws DbException, NotFoundException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            Platform platform = DaoFactory.getPlatformDao().get(platformId);
            Partner partner = DaoFactory.getPartnerDao().getByUserId(userId);
            checkPlatform(platform, partner, transaction);

            transaction.commit();
            return platform;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    private static void checkPlatform(Platform platform, Partner partner, Transaction transaction)
            throws NotFoundException {
        if (platform == null
                || partner == null
                || !platform.getPartner().getId().equals(partner.getId())){
            DbAssistant.transactionRollback(transaction);
            throw new NotFoundException();
        }
    }

    public static void update(Platform platform) throws DbException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            DaoFactory.getPlatformDao().update(platform);
            transaction.commit();
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    public static Platform updateExcludeNullWithChecking(
            long userId, long platformId, @NotNull Object platformFromClient
    )
            throws DbException, NotFoundException, ServiceException {
        Transaction transaction = DbAssistant.getTransaction();
        Platform platformFromBase = null;
        try {
            platformFromBase = DaoFactory.getPlatformDao().get(platformId);
            Partner partner = DaoFactory.getPartnerDao().getByUserId(userId);
            checkPlatform(platformFromBase, partner, transaction);

            NullAware.getInstance().copyProperties(platformFromBase, platformFromClient);
            DaoFactory.getPlatformDao().update(platformFromBase);

            transaction.commit();
            return platformFromBase;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            DbAssistant.transactionRollback(transaction);
            throw new ServiceException("Error copy objects: "
                    + platformFromClient.toString() + " to " + platformFromBase.toString(), e);
        }
    }

    public static void delete(long id) throws DbException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            DaoFactory.getPlatformDao().delete(id);
            transaction.commit();
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    public static void deleteWithChecking(long userId, long platformId) throws DbException, NotFoundException {
        Transaction transaction = DbAssistant.getTransaction();
        Platform platformFromBase;
        try {
            platformFromBase = DaoFactory.getPlatformDao().get(platformId);
            Partner partner = DaoFactory.getPartnerDao().getByUserId(userId);
            checkPlatform(platformFromBase, partner, transaction);

            DaoFactory.getPlatformDao().delete(platformId);
            transaction.commit();
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }
}
