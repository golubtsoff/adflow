package service;

import dao.DaoFactory;
import dao.DbAssistant;
import entity.users.PictureFormat;
import exception.ConflictException;
import exception.DbException;
import exception.NotFoundException;
import exception.ServiceException;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import rest.admin.PictureFormatResource;
import util.NullAware;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.validation.constraints.NotNull;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static dao.impl.PictureFormatDao.CAN_NOT_BE_USED;
import static dao.impl.PictureFormatDao.IS_CAN_BE_USED;

public abstract class PictureFormatService {

    public static PictureFormat create(@NotNull Object pictureFormatDto)
            throws DbException, ServiceException {
        Transaction transaction = DbAssistant.getTransaction();
        PictureFormatResource.PictureFormatDto pfDto = (PictureFormatResource.PictureFormatDto) pictureFormatDto;
        PictureFormat pictureFormat = new PictureFormat();
        try {
            NullAware.getInstance().copyProperties(pictureFormat, pictureFormatDto);
            List<PictureFormat> pictureFormats = DaoFactory.getPictureFormatDao().get(pictureFormat);
            if (pictureFormats.isEmpty()){
                pictureFormat.setCanBeUsed(IS_CAN_BE_USED);
                DaoFactory.getPictureFormatDao().create(pictureFormat);
            } else {
                pictureFormat = pictureFormats.get(0);
                if (!pictureFormat.isCanBeUsed()){
                    pictureFormat.setCanBeUsed(IS_CAN_BE_USED);
                    DaoFactory.getPictureFormatDao().update(pictureFormat);
                }
            }
            transaction.commit();
            return pictureFormat;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            DbAssistant.transactionRollback(transaction);
            throw new ServiceException("Error copy objects: "
                    + pictureFormatDto.toString() + " to " + pictureFormat.toString(), e);
        }
    }

    public static List<PictureFormat> getAll() throws DbException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            List<PictureFormat> formats = DaoFactory.getPictureFormatDao().getAll();
            transaction.commit();
            return formats;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    public static List<PictureFormat> getCanBeUsedFormats() throws DbException {
        return getFormatsByCondition(IS_CAN_BE_USED);
    }

    public static List<PictureFormat> getMarkedForDeleteFormats() throws DbException {
        return getFormatsByCondition(CAN_NOT_BE_USED);
    }

    private static List<PictureFormat> getFormatsByCondition(boolean canBeUsed) throws DbException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            List<PictureFormat> formats;
            if (canBeUsed){
                formats = DaoFactory.getPictureFormatDao().getCanBeUsedFormats();
            } else {
                formats = DaoFactory.getPictureFormatDao().getMarkedForDeleteFormats();
            }
            transaction.commit();
            return formats;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    public static PictureFormat get(long id) throws DbException, NotFoundException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            PictureFormat pictureFormat = DaoFactory.getPictureFormatDao().get(id);
            if (pictureFormat == null) {
                DbAssistant.transactionRollback(transaction);
                throw new NotFoundException("Picture's format with id=" + String.valueOf(id) + " not found");
            }
            transaction.commit();
            return pictureFormat;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    public static void delete(long id)
            throws DbException, NotFoundException, ConflictException {

        Transaction transaction = DbAssistant.getTransaction();
        try {
            PictureFormat pictureFormat = DaoFactory.getPictureFormatDao().get(id);
            if (pictureFormat == null){
                DbAssistant.transactionRollback(transaction);
                throw new NotFoundException("Picture's format with id=" + String.valueOf(id) + " not found");
            }
            if (pictureFormat.isCanBeUsed()){
                pictureFormat.setCanBeUsed(CAN_NOT_BE_USED);
                DaoFactory.getPictureFormatDao().update(pictureFormat);
            }
            transaction.commit();

            transaction = DbAssistant.getTransaction();
            DaoFactory.getPictureFormatDao().delete(id);
            transaction.commit();
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        } catch (PersistenceException e){
            DbAssistant.transactionRollback(transaction);
            throw new ConflictException(e);
        }
    }
}
