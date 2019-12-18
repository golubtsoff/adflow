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
import util.Links;
import util.NullAware;

import javax.imageio.ImageIO;
import javax.persistence.NoResultException;
import javax.validation.constraints.NotNull;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class CampaignService {

    public static Campaign create(long userId, @NotNull Object campaignDto, Map<PictureFormat, BufferedImage> images)
            throws NotFoundException, DbException, ServiceException, ConflictException, IOException {
        Transaction transaction = DbAssistant.getTransaction();
        Campaign campaign = null;
        try {
            Customer customer = DaoFactory.getCustomerDao().getByUserId(userId);
            if (customer == null){
                DbAssistant.transactionRollback(transaction);
                throw new NotFoundException("Customer with user's id=" + userId + " not found");
            }
            Hibernate.initialize(customer.getCampaigns());

            if (campaignDto instanceof rest.customer.CampaignResource.CampaignDto) {
                rest.customer.CampaignResource.CampaignDto campaignDtoCast
                        = (rest.customer.CampaignResource.CampaignDto) campaignDto;
                checkExistedTitle(customer.getCampaigns(), campaignDtoCast.getTitle());
            }

            campaign = new Campaign(customer);
            NullAware.getInstance().copyProperties(campaign, campaignDto);
            DaoFactory.getCampaignDao().create(campaign);
            campaign.setPictures(saveImages(customer.getId(), campaign.getId(), images));

            transaction.commit();
            return campaign;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            DbAssistant.transactionRollback(transaction);
            throw new ServiceException("Error copy objects: "
                    + campaignDto.toString() + " to " + campaign.toString(), e);
        } catch (ConflictException | IOException e){
            DbAssistant.transactionRollback(transaction);
            throw e;
        }
    }

    private static Set<Picture> saveImages(
        long customerId,
        long campaignId,
        Map<PictureFormat, BufferedImage> images) throws IOException, ConflictException {

        if (images == null) return null;
        checkPictureFormats(images.keySet());
        String link = Links.createFoldersIfNotExist(customerId, campaignId);
        Set<Picture> pictures = new HashSet<>();
        for (Map.Entry<PictureFormat, BufferedImage> entry : images.entrySet()){
            PictureFormat format = DaoFactory.getPictureFormatDao().get(entry.getKey()).get(0);
            String filename = customerId + "_" + campaignId + "_" + format.getId() + "." + Picture.FORMAT_NAME_FILE_EXTENSION;
            try(OutputStream os = Files.newOutputStream(Paths.get(link, filename))){
                ImageIO.write(entry.getValue(), Picture.FORMAT_NAME_FILE_EXTENSION, os);
                os.flush();
            }
            pictures.add(new Picture(filename, format));
        }
        return pictures;
    }

    private static void checkPictureFormats(Set<PictureFormat> pictureFormats) throws ConflictException {
        List<PictureFormat> formatList = DaoFactory.getPictureFormatDao().getCanBeUsedFormats();
        Set<PictureFormat> canBeUsedFormats = new HashSet<>(formatList);
        for (PictureFormat format : pictureFormats){
            if (!canBeUsedFormats.contains(format))
                throw new ConflictException("Picture's format not allowed: " + format.toString());
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

    public static Campaign get(long userId, long campaignId) throws DbException, NotFoundException {
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

    public static Campaign updateByAdmin(long userId, long campaignId, @NotNull Object campaignDto)
            throws DbException, NotFoundException, ServiceException, ConflictException{
        Transaction transaction = DbAssistant.getTransaction();
        Campaign campaign = null;
        try {
            campaign = checkAndGetCampaign(campaignId, userId);
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

    public static Campaign updateByCustomer(
            long userId, long campaignId, @NotNull Object campaignDto, Map<PictureFormat, BufferedImage> images)
            throws DbException, NotFoundException, ServiceException, ConflictException, IOException {
        Transaction transaction = DbAssistant.getTransaction();
        Campaign campaign = null;
        try {
            campaign = checkAndGetCampaign(campaignId, userId);

            if (campaignDto instanceof rest.customer.CampaignResource.CampaignDto) {
                if (campaign.getStatus() == Status.REMOVED){
                    throw new NotFoundException();
                }
                rest.customer.CampaignResource.CampaignDto campaignDtoCast
                        = (rest.customer.CampaignResource.CampaignDto) campaignDto;

                if (!campaign.getTitle().equals(campaignDtoCast.getTitle())){
                    checkExistedTitle(campaign.getCustomer().getCampaigns(), campaignDtoCast.getTitle());
                }
            }

            NullAware.getInstance().copyProperties(campaign, campaignDto);
            DaoFactory.getCampaignDao().update(campaign);
            updateImages(campaign, images);

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
        } catch (IOException | ConflictException e){
            DbAssistant.transactionRollback(transaction);
            throw e;
        }
    }

    private static void updateImages(Campaign campaign, Map<PictureFormat, BufferedImage> images)
            throws IOException, ConflictException {
        deletePicturesOfCampaign(campaign);
        if (images == null || images.size() == 0) return;
        campaign.setPictures(saveImages(campaign.getCustomer().getId(), campaign.getId(), images));
    }

    private static void deletePicturesOfCampaign(Campaign campaign) throws IOException {
        if (campaign.getPictures() == null) return;
        Links.deleteFolder(campaign.getCustomer().getId(), campaign.getId());
        campaign.setPictures(null);
    }

    public static void delete(long userId, long campaignId) throws DbException, NotFoundException, IOException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            Campaign campaign = checkAndGetCampaign(campaignId, userId);
            deletePicturesOfCampaign(campaign);
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
        Campaign campaign;
        try {
            campaign = checkAndGetCampaign(campaignId, userId);
            campaign.setStatus(Status.REMOVED);
            DaoFactory.getCampaignDao().update(campaign);
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
}
