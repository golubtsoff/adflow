package dao.impl;

import dao.AbstractDao;
import dao.DbAssistant;
import entity.statistics.Request;
import entity.users.customer.Campaign;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class RequestDao extends AbstractDao<Request> {

    public RequestDao(){
        super(Request.class);
    }

//    public BigDecimal getCostRequestByDate(Campaign campaign, Date date){
//        DbAssistant.getSessionFactory()
//                .getCurrentSession()
//                .createQuery("from " + Request.class.getSimpleName()
//                        + " where " + fieldName + " = :value", parameterizedClass)
//                .setParameter("value", value)
//                .getSingleResult();
//    }
}
