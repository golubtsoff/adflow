package main;

import dao.CustomerPartnerDao;
import dao.Dao;
import dao.DaoFactory;
import dao.DbAssistant;
import entity.users.Account;
import entity.users.Accountable;
import entity.users.partner.Partner;
import entity.users.user.Person;
import entity.users.user.Role;
import entity.users.user.User;
import entity.users.user.UserToken;
import exception.DbException;
import exception.NotFoundException;
import exception.ServiceException;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import service.UserService;
import util.NullAware;

import javax.persistence.NoResultException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Main {

    public static void main(String[] args) throws DbException, ServiceException, IllegalAccessException, NotFoundException, InvocationTargetException {

        foo();

        LocalDateTime ldt = LocalDateTime.now();
        LocalDateTime ldt2 = ldt.plusMinutes(15);
        long minutes = ChronoUnit.MINUTES.between(ldt, ldt2);

        UserService.signUpExceptAdministrator("customer", "123", "customer");
        UserService.signUpExceptAdministrator("partner", "123", "partner");
        UserService.signUpExceptAdministrator("admin", "123", "ADMIN");
        UserService.signUpExceptAdministrator("admin", "123", "admin");
        UserService.signUp("admin", "123", Role.ADMIN);
        UserToken token = UserService.signIn("partner", "123");
//        assert (token != null);
//        UserService.signOut(token.getUser().getId());
        User user = UserService.get(1);

        bar(user, new Account(BigDecimal.valueOf(40L)));

        DbAssistant.close();
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
