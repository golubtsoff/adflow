package dao.impl;

import dao.AbstractDao;
import entity.users.customer.Customer;

public class CustomerDao extends AbstractDao<Customer> {

    public CustomerDao(){
        super(Customer.class);
    }
}
