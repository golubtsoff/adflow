package dao.impl;

import dao.AbstractDao;
import dao.DaoFactory;
import entity.users.customer.Campaign;
import entity.users.customer.Customer;

import java.util.ArrayList;
import java.util.List;

public class CampaignDao extends AbstractDao<Campaign> {

    public CampaignDao(){
        super(Campaign.class);
    }

    public List<Campaign> getAllByCustomerId(Long customerId) {
        return getAll(Campaign.CUSTOMER_ID, customerId.toString());
    }
}
