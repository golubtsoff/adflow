package service;

import dao.DaoFactory;
import dao.DbAssistant;
import entity.statistics.Request;
import entity.statistics.Session;
import entity.users.PictureFormat;
import entity.users.Status;
import entity.users.partner.Partner;
import entity.users.partner.Platform;
import entity.users.partner.PlatformToken;
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

public abstract class PlatformService {

    public static Platform create(long userId, @NotNull Object platformDto)
            throws Exception {
        Transaction transaction = DbAssistant.getTransaction();
        Platform platform = null;
        try {
            Partner partner = DaoFactory.getPartnerDao().getByUserId(userId);
            if (partner == null){
                DbAssistant.transactionRollback(transaction);
                throw new NotFoundException("Partner with user's id=" + String.valueOf(userId) + " not found");
            }
            Hibernate.initialize(partner.getPlatforms());

            if (platformDto instanceof PlatformResource.PlatformDto) {
                PlatformResource.PlatformDto platformDtoCast = (PlatformResource.PlatformDto) platformDto;
                checkExistedTitle(partner.getPlatforms(), platformDtoCast.getTitle());
                checkAndPersistPictureFormat(platformDtoCast);
            }

            platform = new Platform(partner);
            NullAware.getInstance().copyProperties(platform, platformDto);
            DaoFactory.getPlatformDao().create(platform);

            PlatformToken platformToken = new PlatformToken(platform);
            DaoFactory.getPlatformTokenDao().create(platformToken);

            transaction.commit();
            return platform;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            DbAssistant.transactionRollback(transaction);
            throw new ServiceException("Error copy objects: "
                    + platformDto.toString() + " to " + platform.toString(), e);
        }
    }

    private static void checkExistedTitle(Set<Platform> platforms, String title) throws ConflictException {
        for (Platform platform : platforms){
            if (platform.getTitle().equals(title)){
                throw new ConflictException("Title '" + title + "' is already used");
            }
        }
    }

    private static void checkAndPersistPictureFormat(PlatformResource.PlatformDto platformDto)
            throws NotFoundException {
        List<PictureFormat> formats = DaoFactory.getPictureFormatDao().get(platformDto.getPictureFormat());
        if (formats.isEmpty() || !formats.get(0).isCanBeUsed()){
            throw new NotFoundException("Unknown picture's format: " + platformDto.getPictureFormat());
        }
        platformDto.setPictureFormat(formats.get(0));
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
            Platform platform = checkAndGetPlatform(platformId, userId);
            Hibernate.initialize(platform.getPartner());
            transaction.commit();
            return platform;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        } catch (NotFoundException e){
            DbAssistant.transactionRollback(transaction);
            throw new NotFoundException(e);
        }
    }

    private static Platform checkAndGetPlatform(long platformId, long userId)
            throws NotFoundException {
        Platform platform = DaoFactory.getPlatformDao().get(platformId);
        Partner partner = DaoFactory.getPartnerDao().getByUserId(userId);
        if (platform == null
                || partner == null
                || !platform.getPartner().getId().equals(partner.getId())){
            throw new NotFoundException();
        }
        return platform;
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
            long userId, long platformId, @NotNull Object platformDto)
            throws DbException, NotFoundException, ServiceException, ConflictException {
        Transaction transaction = DbAssistant.getTransaction();
        Platform platform = null;
        try {
            platform = checkAndGetPlatform(platformId, userId);

            if (platformDto instanceof rest.partner.PlatformResource.PlatformDto) {
                if (platform.getStatus() == Status.REMOVED){
                    throw new NotFoundException();
                }
                rest.partner.PlatformResource.PlatformDto platformDtoCast
                        = (rest.partner.PlatformResource.PlatformDto) platformDto;

                if (!platform.getTitle().equals(platformDtoCast.getTitle())){
                    checkExistedTitle(platform.getPartner().getPlatforms(), platformDtoCast.getTitle());
                }
                checkAndPersistPictureFormat(platformDtoCast);
            }

            NullAware.getInstance().copyProperties(platform, platformDto);
            DaoFactory.getPlatformDao().update(platform);

            transaction.commit();
            return platform;
        }catch (NotFoundException e){
            DbAssistant.transactionRollback(transaction);
            throw new NotFoundException(e);
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            DbAssistant.transactionRollback(transaction);
            throw new ServiceException("Error copy objects: "
                    + platformDto.toString() + " to " + platform.toString(), e);
        }
    }

    public static void delete(long userId, long platformId) throws DbException, NotFoundException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            checkAndGetPlatform(platformId, userId);
            DaoFactory.getPlatformTokenDao().delete(platformId);
            List<Session> sessions = DaoFactory.getSessionDao().getByPlatformId(platformId);
            for (Session session : sessions){
                List<Request> requests = DaoFactory.getRequestDao().getBySessionId(session.getId());
                for (Request request : requests){
                    DaoFactory.getRequestDao().delete(request.getId());
                }
                DaoFactory.getSessionDao().delete(session.getId());
            }
            DaoFactory.getPlatformDao().delete(platformId);
            transaction.commit();
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        } catch (NotFoundException e){
            DbAssistant.transactionRollback(transaction);
            throw new NotFoundException(e);
        }
    }

    public static Platform setStatusRemovedWithChecking(long userId, long platformId)
            throws NotFoundException, DbException {
        Transaction transaction = DbAssistant.getTransaction();
        Platform platform;
        try {
            platform = checkAndGetPlatform(platformId, userId);
            if (platform.getStatus() == Status.REMOVED){
                return platform;
            }
            platform.setStatus(Status.REMOVED);
            DaoFactory.getPlatformDao().update(platform);

            DaoFactory.getPlatformTokenDao().delete(platformId);
            transaction.commit();
            return platform;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        } catch (NotFoundException e){
            DbAssistant.transactionRollback(transaction);
            throw new NotFoundException(e);
        }
    }

    public static PlatformToken getOrCreateToken(Platform platform) throws DbException, ServiceException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            PlatformToken token = DaoFactory.getPlatformTokenDao().get(platform.getId());
            if (token == null){
                token = new PlatformToken(platform);
                DaoFactory.getPlatformTokenDao().create(token);
            }

            transaction.commit();
            return token;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        } catch (Exception e){
            DbAssistant.transactionRollback(transaction);
            throw new ServiceException(e);
        }
    }

    public static PlatformToken getToken(long platformId) throws DbException, ServiceException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            PlatformToken token = DaoFactory.getPlatformTokenDao().get(platformId);
            transaction.commit();
            return token;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        } catch (Exception e){
            DbAssistant.transactionRollback(transaction);
            throw new ServiceException(e);
        }
    }

    public static PlatformToken updateToken(Platform platform) throws DbException, ServiceException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            PlatformToken token = DaoFactory.getPlatformTokenDao().get(platform.getId());
            if (token == null){
                return null;
            }

            token.updateToken();
            DaoFactory.getPlatformTokenDao().update(token);
            transaction.commit();
            return token;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        } catch (Exception e){
            DbAssistant.transactionRollback(transaction);
            throw new ServiceException(e);
        }
    }

}
