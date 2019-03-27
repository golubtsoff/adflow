package service.impl;

import entity.users.customer.Customer;
import service.AbstractUserService;

public class CustomerService extends AbstractUserService<Customer> {

    public CustomerService(){
        super(Customer.class);
    }
}
