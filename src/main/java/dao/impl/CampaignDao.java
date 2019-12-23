package dao.impl;

import dao.AbstractDao;
import dao.DaoFactory;
import dao.DbAssistant;
import entity.users.Action;
import entity.users.customer.Campaign;
import entity.users.customer.Customer;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CampaignDao extends AbstractDao<Campaign> {

    public CampaignDao(){
        super(Campaign.class);
    }

    public List<Campaign> getAllByCustomerId(Long customerId) {
        return getAll(Campaign.CUSTOMER_ID, customerId);
    }

    public List<Campaign> getAllByAction(Action action){
        return getAll(Campaign.ACTION, action);
    }

    public void updateCampaignActionPauseToRun(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.with(LocalTime.MIN);
        LocalDateTime to = now.with(LocalTime.MAX);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String sql =
            "UPDATE adflow.campaigns c\n" +
            "SET c.action = 'RUN'\n" +
            "WHERE c.id in\n" +
            "    (select cid from\n" +
            "        (select c.id cid,\n" +
            "            c2.BALANCE < (c.CPM_RATE / 1000) balance_is_over,\n" +
            "            c.DAILY_BUDGET < ((COALESCE(SUM(r.CAMPAIGN_CPM_RATE), 0) + c.CPM_RATE) / 1000) budget_is_over\n" +
            "        from campaigns c\n" +
            "        left join requests r on c.id = r.campaign_id\n" +
            "             AND r.CREATION_TIME BETWEEN '" + from.format(formatter) + "'\n" +
            "             AND '" + to.format(formatter) + "'\n" +
            "        left join customers c2 on c.customer_id = c2.id\n" +
            "        left join users u on c2.user_id = u.user_id and u.status = 'WORKING'\n" +
            "        where c.action = 'PAUSE' and c.status = 'WORKING'\n" +
            "        group by c.id) q\n" +
            "    where q.budget_is_over = 0 and q.balance_is_over = 0)";
        DbAssistant.getSessionFactory().getCurrentSession().createNativeQuery(sql).executeUpdate();
    }

}
