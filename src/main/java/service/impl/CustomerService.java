package service.impl;

import entity.users.customer.Customer;
import service.AbstractConcreteRoleService;

@Deprecated
public class CustomerService extends AbstractConcreteRoleService<Customer> {

    public CustomerService(){
        super(Customer.class);
    }
}
