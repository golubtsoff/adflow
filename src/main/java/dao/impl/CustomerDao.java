package dao.impl;

import dao.AbstractDao;
import dao.ClientDao;
import entity.users.customer.Customer;

public class CustomerDao extends AbstractDao<Customer> implements ClientDao<Customer> {

    public CustomerDao(){
        super(Customer.class);
    }

    public Customer getByUserId(Long userId) {
        return get("user_id", userId);
    }
}
