package main;

import dao.DbAssistant;
import entity.users.user.Role;
import exception.DbException;
import exception.ServiceException;
import service.UserService;
import service.UserServiceFactory;
import service.impl.AdministratorService;
import service.impl.CustomerService;
import service.impl.PartnerService;

public class Main {
    public static void main(String[] args) throws DbException, ServiceException {
        AdministratorService administratorService = UserServiceFactory.getAdministratorService();
        administratorService.signUp("admin", "123", Role.ADMIN);
        administratorService.signUp("admin", "123", Role.ADMIN);

        PartnerService partnerService = UserServiceFactory.getPartnerService();
        partnerService.signUp("partner", "123", Role.PARTNER);

        CustomerService customerService = UserServiceFactory.getCustomerService();
        customerService.signUp("custom", "123", Role.CUSTOMER);

        DbAssistant.close();
    }
}
