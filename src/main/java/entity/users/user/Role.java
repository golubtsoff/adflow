package entity.users.user;

import entity.users.Administrator;
import entity.users.ConcreteUser;
import entity.users.customer.Customer;
import entity.users.partner.Partner;

public enum Role {
    CUSTOMER(Customer.class),
    ADMIN(Administrator.class),
    PARTNER(Partner.class);

    private Class parametrizedClass;

    <T extends ConcreteUser> Role(Class<T> parametrizedClass){
        this.parametrizedClass = parametrizedClass;
    }

    public Class getParametrizedClass() {
        return parametrizedClass;
    }

    public static <T extends ConcreteUser> Role getRole(Class<T> cl){
        for(Role role : Role.values()){
            if (cl == role.getParametrizedClass())
                return role;
        }
        return null;
    }
}

