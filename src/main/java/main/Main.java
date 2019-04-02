package main;

import dao.DbAssistant;
import entity.users.user.User;
import exception.DbException;
import exception.ServiceException;
import service.ConcreteRoleServiceFactory;
import service.UserService;
import service.impl.AdministratorService;
import service.impl.CustomerService;
import service.impl.PartnerService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Main {
    public static void main(String[] args) throws DbException, ServiceException {

        LocalDateTime ldt = LocalDateTime.now();
        LocalDateTime ldt2 = ldt.plusMinutes(15);
        long minutes = ChronoUnit.MINUTES.between(ldt, ldt2);

        UserService.signUp("customer", "123", "customer");
        UserService.signUp("partner", "123", "partner");
        UserService.signUp("admin", "123", "ADMIN");
        UserService.signUp("admin", "123", "admin");
        UserService.signUp("admin2", "123", "ADMIN");
        User user2 = UserService.signIn("partner", "123");

        DbAssistant.close();
    }
}
