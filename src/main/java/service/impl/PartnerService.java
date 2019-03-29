package service.impl;

import dao.DaoFactory;
import dao.DbAssistant;
import entity.users.customer.Customer;
import entity.users.partner.Partner;
import entity.users.user.Role;
import entity.users.user.User;
import exception.DbException;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import service.AbstractUserService;
import util.Hash;

import javax.persistence.NoResultException;

public class PartnerService extends AbstractUserService<Partner> {

    public PartnerService(){
        super(Partner.class);
    }
}
