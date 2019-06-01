package dao.impl;

import dao.AbstractDao;
import dao.CustomerPartnerDao;
import entity.users.customer.Customer;
import entity.users.partner.Partner;

public class CustomerDao extends AbstractDao<Customer> implements CustomerPartnerDao<Customer> {

    public CustomerDao(){
        super(Customer.class);
    }

    public Customer getByUserId(Long userId) {
        return get("USER_ID", userId.toString());
    }
}
