package service;

import service.impl.AdministratorService;
import service.impl.CustomerService;
import service.impl.PartnerService;

import java.util.Arrays;
import java.util.List;

public abstract class ConcreteRoleServiceFactory {
    private static final List<ConcreteRoleService> userServiceList = Arrays.asList(
            new PartnerService(),
            new CustomerService(),
            new AdministratorService()
    );

    private static <T extends ConcreteRoleService> T getDao(Class<T> cl){
        for(ConcreteRoleService userService : userServiceList){
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
