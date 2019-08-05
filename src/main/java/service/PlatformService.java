package service;

import dao.DaoFactory;
import dao.DbAssistant;
import entity.users.customer.Picture;
import entity.users.PictureFormat;
import entity.users.partner.Partner;
import entity.users.partner.Platform;
import exception.ConflictException;
import exception.DbException;
import exception.NotFoundException;
import exception.ServiceException;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import rest.partner.PlatformResource;
import util.NullAware;

import javax.persistence.NoResultException;
import javax.validation.constraints.NotNull;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;

public class PlatformService {

//    public static Platform create(long userId, @NotNull Object platformDto)
//            throws NotFoundException, DbException, ServiceException, ConflictException {
//        Transaction transaction = DbAssistant.getTransaction();
//        Platform platform = null;
//        try {
//            Partner partner = DaoFactory.getPartnerDao().getByUserId(userId);
//            if (partner == null){
//                DbAssistant.transactionRollback(transaction);
//                throw new NotFoundException("Partner with user's id=" + String.valueOf(userId) + " not found");
//            }
//            Hibernate.initialize(partner.getPlatforms());
//            checkPlatformDtoByPartner(partner, platformDto);
//
//            platform = new Platform(partner);
//            NullAware.getInstance().copyProperties(platform, platformDto);
//            DaoFactory.getPlatformDao().create(platform);
//            transaction.commit();
//            return platform;
//        } catch (HibernateException | NoResultException | NullPointerException e) {
//            DbAssistant.transactionRollback(transaction);
//            throw new DbException(e);
//        } catch (IllegalAccessException | InvocationTargetException e) {
//            DbAssistant.transactionRollback(transaction);
//            throw new ServiceException("Error copy objects: "
//                    + platformDto.toString() + " to " + platform.toString(), e);
//        }
//    }

//    private static void checkPlatformDtoByPartner(@NotNull Partner partner, @NotNull Object platformDto)
//            throws ConflictException, NotFoundException {
//        if (platformDto instanceof PlatformResource.PlatformDto) {
//            PlatformResource.PlatformDto platformDtoCast
//                    = (PlatformResource.PlatformDto) platformDto;
//
//            if (titleIsExist(partner.getPlatforms(), platformDtoCast.getTitle())){
//                throw new ConflictException("Title '" + platformDtoCast.getTitle() + "' is already used");
//            }
//
//            if (platformDtoCast.getPictures() != null){
//                List<PictureFormat> formats = DaoFactory.getPictureFormatDao().getAll();
//                Set<Picture> pictures = platformDtoCast.getPictures();
//                checkAndPersistFormats(pictures, formats);
//            }
//        }
//    }

    private static boolean titleIsExist(Set<Platform> platforms, String title){
        for (Platform platform : platforms){
            if (platform.getTitle().equals(title)){
                return true;
            }
        }
        return false;
    }

    private static void checkAndPersistFormat(Set<Picture> pictures, List<PictureFormat> formats)
            throws NotFoundException {
        for (Picture picture : pictures) {
            boolean formatIsExist = false;
            for (PictureFormat pictureFormat : formats) {
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
