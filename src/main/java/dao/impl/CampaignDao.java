package dao.impl;

import dao.AbstractDao;
import entity.users.customer.Campaign;

public class CampaignDao extends AbstractDao<Campaign> {

    public CampaignDao(){
        super(Campaign.class);
    }
}
