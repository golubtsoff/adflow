package main;

import dao.CustomerPartnerDao;
import dao.DaoFactory;
import dao.DbAssistant;
import entity.users.Account;
import entity.users.Accountable;
import entity.users.Status;
import entity.users.customer.Campaign;
import entity.users.customer.Customer;
import entity.users.customer.Picture;
import entity.users.PictureFormat;
import entity.users.user.Person;
import entity.users.user.Role;
import entity.users.user.User;
import exception.ConflictException;
import exception.DbException;
import exception.NotFoundException;
import exception.ServiceException;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
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
            throws DbException, ServiceException, NotFoundException, ConflictException {
        initData();
//        deletePictureFormat(new PictureFormat(800, 600));
        DbAssistant.close();
    }

    public static void initData() throws DbException, ServiceException, NotFoundException {
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

        Transaction transaction = DbAssistant.getTransaction();
        try {
            Customer customer_1 = DaoFactory.getCustomerDao().getByUserId(userCustomer_1.getId());
            Campaign campaign_1 = new Campaign(
                    customer_1,
                    "Campaign_1",
                    "Title of Campaign_1",
                    "http://somesite.come",
                    BigDecimal.valueOf(100),
                    BigDecimal.valueOf(57)
            );
            PictureFormat pictureFormat = new PictureFormat(800, 600);
            DaoFactory.getPictureFormatDao().create(pictureFormat);
            Picture picture = new Picture("filename.jpg", pictureFormat);
            campaign_1.addPicture(picture);
            DaoFactory.getCampaignDao().create(campaign_1);

            transaction.commit();
        } catch (HibernateException | NoResultException | NullPointerException e) {
                DbAssistant.transactionRollback(transaction);
                throw new DbException(e);
        }

        Customer customer = getCustomer(userCustomer_1.getId());
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

    private static void deletePictureFormat(PictureFormat pictureFormat)
            throws DbException, NotFoundException, ConflictException {
        PictureFormatService.delete(1);
    }

    public static void foo(){
        User user1 = new User("admin", "987", Role.ADMIN);
        User user2 = new User();
        Person person = new Person("Павел", "Иванов");
        user2.setPerson(person);
        AnotherUser anotherUser = new AnotherUser();

        try {
            NullAware.getInstance().copyProperties(user1, user2);
            NullAware.getInstance().copyProperties(user2, anotherUser);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    public static void bar(User userFromBase, Account accountFromClient)
            throws NotFoundException, InvocationTargetException, IllegalAccessException, DbException {
        Transaction transaction = DbAssistant.getTransaction();

        Account accountFromBase;
        CustomerPartnerDao dao;
        Accountable accountable;
        try {
            if (userFromBase.getRole() == Role.PARTNER) {
                dao = DaoFactory.getPartnerDao();
            } else if (userFromBase.getRole() == Role.CUSTOMER) {
                dao = DaoFactory.getCustomerDao();
            } else {
                throw new NotFoundException();
            }

            accountable = (Accountable) dao.getByUserId(userFromBase.getId());
            accountFromBase = accountable.getAccount();
            NullAware.getInstance().copyProperties(accountFromBase, accountFromClient);
            accountable.setAccount(accountFromBase);
            dao.update(accountable);
            Class cl = accountable.getClass();

            transaction.commit();
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            DbAssistant.transactionRollback(transaction);
        }
    }
}
