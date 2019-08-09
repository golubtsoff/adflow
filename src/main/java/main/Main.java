package main;

import dao.CustomerPartnerDao;
import dao.DaoFactory;
import dao.DbAssistant;
import entity.users.*;
import entity.users.customer.Campaign;
import entity.users.customer.Customer;
import entity.users.customer.Picture;
import entity.users.partner.Partner;
import entity.users.partner.Platform;
import entity.users.partner.PlatformToken;
import entity.users.user.Person;
import entity.users.user.Role;
import entity.users.user.User;
import exception.ConflictException;
import exception.DbException;
import exception.NotFoundException;
import exception.ServiceException;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import service.CampaignService;
import service.PictureFormatService;
import service.UserService;
import util.NullAware;

import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public class Main {

    public static void main(String[] args)
            throws Exception {
        initData();
        DbAssistant.close();
    }

    public static void initData() throws Exception {
        User userCustomer_1 = UserService.signUpExceptAdministrator("customer_1", "123", "customer");
        User userPartner_1 = UserService.signUpExceptAdministrator("partner_1", "123", "partner");
        User userAdmin_1 = UserService.signUp("admin_1", "123", Role.ADMIN);

        assert userCustomer_1 != null;
        userCustomer_1.setStatus(Status.WORKING);
        UserService.update(userCustomer_1);
        assert userPartner_1 != null;
        userPartner_1.setStatus(Status.WORKING);
        UserService.update(userPartner_1);
        assert userAdmin_1 != null;
        userAdmin_1.setStatus(Status.WORKING);
        UserService.update(userAdmin_1);

        PictureFormat pictureFormat = new PictureFormat(800, 600);
        createPictureFormat(pictureFormat);

        Picture picture = new Picture("filename.jpg", pictureFormat);

        Campaign campaign = new Campaign(
                null,
                "Campaign_1",
                "Title of Campaign_1",
                "http://somesite.come",
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(57),
                Action.RUN,
                Status.WORKING
        );
        createCampaign(userCustomer_1, pictureFormat, picture, campaign);

        Platform platform = new Platform(
                null,
                "Platform_1",
                "Title of Platform_1",
                BigDecimal.valueOf(100),
                pictureFormat,
                Action.RUN,
                Status.WORKING
        );
        createPlatform(userPartner_1, platform);
    }

    private static Platform createPlatform(User userPartner, Platform platform) throws Exception {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            Partner partner = DaoFactory.getPartnerDao().getByUserId(userPartner.getId());
            platform.setPartner(partner);

            DaoFactory.getPlatformDao().create(platform);
            PlatformToken platformToken = new PlatformToken(platform);
            DaoFactory.getPlatformTokenDao().create(platformToken);

            transaction.commit();
            return platform;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    private static Campaign createCampaign(
            User userCustomer,
            PictureFormat pictureFormat,
            Picture picture,
            Campaign campaign
    ) throws DbException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            Customer customer = DaoFactory.getCustomerDao().getByUserId(userCustomer.getId());
            campaign.setCustomer(customer);

            campaign.addPicture(picture);
            DaoFactory.getCampaignDao().create(campaign);

            transaction.commit();
            return campaign;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }


    private static PictureFormat createPictureFormat(PictureFormat pictureFormat) throws DbException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            DaoFactory.getPictureFormatDao().create(pictureFormat);
            transaction.commit();
            return pictureFormat;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    private static Customer getCustomer(long userId) throws DbException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            Customer customer = DaoFactory.getCustomerDao().getByUserId(userId);
            Set<Campaign> campaigns = customer.getCampaigns();
            transaction.commit();
            return customer;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

}
