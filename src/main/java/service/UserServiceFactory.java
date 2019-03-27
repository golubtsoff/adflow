package service;

import service.impl.AdministratorService;
import service.impl.CustomerService;
import service.impl.PartnerService;

import java.util.Arrays;
import java.util.List;

public abstract class UserServiceFactory {
    private static final List<UserService> userServiceList = Arrays.asList(
            new PartnerService(),
            new CustomerService(),
            new AdministratorService()
    );

    private static <T extends UserService> T getDao(Class<T> cl){
        for(UserService userService : userServiceList){
            if (cl.isInstance(userService))
                return cl.cast(userService);
        }
        return null;
    }

    public static AdministratorService getAdministratorService(){
        return getDao(AdministratorService.class);
    }

    public static CustomerService getCustomerService(){
        return getDao(CustomerService.class);
    }

    public static PartnerService getPartnerService(){
        return getDao(PartnerService.class);
    }

}
