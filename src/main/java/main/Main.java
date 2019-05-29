package main;

import dao.DbAssistant;
import entity.users.user.Person;
import entity.users.user.Role;
import entity.users.user.User;
import entity.users.user.UserToken;
import exception.DbException;
import exception.ServiceException;
import service.ConcreteRoleServiceFactory;
import service.UserService;
import service.impl.AdministratorService;
import service.impl.CustomerService;
import service.impl.PartnerService;
import util.NullAware;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Main {

    public static void main(String[] args) throws DbException, ServiceException {

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
}
