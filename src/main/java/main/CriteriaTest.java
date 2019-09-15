package main;

import dao.DaoFactory;
import dao.DbAssistant;
import entity.statistics.Request;
import entity.users.Action;
import entity.users.customer.Campaign;
import exception.BadRequestException;
import exception.DbException;
import exception.NotFoundException;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class CriteriaTest {

    public static List<Campaign> getCampaigns() throws DbException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            Session session = DbAssistant.getSessionFactory().getCurrentSession();
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

            CriteriaQuery<Campaign> criteriaQuery = criteriaBuilder.createQuery(Campaign.class);
            Root<Campaign> campaignRoot = criteriaQuery.from(Campaign.class);
            criteriaQuery.select(campaignRoot)
                    .where(
                            criteriaBuilder.equal(
                                    campaignRoot.get("action"),Action.RUN
                            )
                    )
                    .orderBy(criteriaBuilder.asc(campaignRoot.get("title")));
            TypedQuery<Campaign> query = session.createQuery(criteriaQuery);

            List<Campaign> campaigns = query.getResultList();
            transaction.commit();
            return campaigns;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

}
