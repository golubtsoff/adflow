package service.impl;

import dao.DaoFactory;
import dao.DbAssistant;
import entity.users.Administrator;
import entity.users.customer.Customer;
import entity.users.user.Role;
import entity.users.user.User;
import exception.DbException;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import service.AbstractUserService;
import util.Hash;

import javax.persistence.NoResultException;

public class CustomerService extends AbstractUserService<Customer> {

    public CustomerService(){
        super(Customer.class);
    }
}
