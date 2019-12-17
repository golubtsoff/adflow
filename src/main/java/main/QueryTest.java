package main;

import dao.DaoFactory;
import dao.DbAssistant;
import entity.statistics.Request;
import entity.users.Action;
import entity.users.PictureFormat;
import entity.users.customer.Campaign;
import entity.users.partner.Platform;
import exception.DbException;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.*;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class QueryTest {

    public static void testPictureFormatDao(){
        Transaction transaction = DbAssistant.getTransaction();
        List<PictureFormat> formats;
        try {
            formats = DaoFactory.getPictureFormatDao().getCanBeUsedFormats();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void printCrtByCompaign() throws DbException {
        List<Tuple> tuples = QueryTest.getListCostRequestTodayByCompany();
        for (Tuple tuple : tuples){
            Campaign campaign = tuple.get("campaign", Campaign.class);
            BigDecimal crt = tuple.get("crt", BigDecimal.class);
            System.out.println(
                    campaign.getId()
                            + " : " + crt
                            + " : " + campaign.getAction()
                            + " : " + campaign.getStatus());
        }

//        List<Tuple> tuples = QueryTest.getListCostRequestTodayByCompany();
//        for (Tuple tuple : tuples){
//            long id = (Long)tuple.get("campaign");
//            BigDecimal crt = tuple.get("crt", BigDecimal.class);
//            System.out.println(id + " : " + crt);
//        }
    }

    public static void printCrtByCompaignJpql() throws DbException {
        List<Tuple> tuples = QueryTest.getListCostRequestTodayByCompanyJpql();
        for (Tuple tuple : tuples){
            Campaign campaign = tuple.get("campaign", Campaign.class);
            BigDecimal crt = tuple.get("crt", BigDecimal.class);
            Integer rbbu = tuple.get("rbbu", Integer.class);
            BigDecimal k = tuple.get("k", BigDecimal.class);
            BigDecimal k_crt = tuple.get("k_crt", BigDecimal.class);
            System.out.println(campaign.getId() + "\t:\t" + crt + "\t:\t" + rbbu + "\t:\t" + k +  "\t:\t" + k_crt);
        }
        System.out.println("Число записей: " + tuples.size());
//        Tuple tuple = QueryTest.getListCostRequestTodayByCompanyJpql();
//        Campaign campaign = tuple.get("campaign", Campaign.class);
//        BigDecimal crt = tuple.get("crt", BigDecimal.class);
//        Integer rbbu = tuple.get("rbbu", Integer.class);
//        BigDecimal k = tuple.get("k", BigDecimal.class);
//        BigDecimal k_crt = tuple.get("k_crt", BigDecimal.class);
//        System.out.println(campaign.getId() + "\t:\t" + crt + "\t:\t" + rbbu + "\t:\t" + k +  "\t:\t" + k_crt);
    }

    public static void printResultJpql() throws DbException {
        List<Result> results = QueryTest.getResultJpql();
        for (Result result : results){
            System.out.println(result);
        }
        System.out.println("Число записей: " + results.size());
    }

    public static void printCrtByCompaignNative() throws DbException {
        List<Tuple> tuples = QueryTest.getListCostRequestTodayByCompanyNative();
        for (Tuple tuple : tuples){
            BigInteger id = tuple.get("id", BigInteger.class);
            BigDecimal crt = tuple.get("crt", BigDecimal.class);
            BigDecimal k = tuple.get("k", BigDecimal.class);
            BigDecimal k_crt = tuple.get("k_crt", BigDecimal.class);
            System.out.println(id + " : " + crt + " : " + k + " : " + k_crt);
        }
        System.out.println("Число записей: " + tuples.size());
    }

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

    public static List<Tuple> getListCostRequestTodayByCompany() throws DbException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            Session session = DbAssistant.getSessionFactory().getCurrentSession();
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();

//            Root<Request> requestRoot = criteriaQuery.from(Request.class);
//            Path<BigDecimal> bigDecimalPath = requestRoot.get("campaignCpmRate");
//            criteriaQuery.multiselect(
//                    requestRoot.get("campaign").alias("campaign"),
//                    criteriaBuilder.sum(bigDecimalPath).alias("crt")
//            ).where(criteriaBuilder.equal(requestRoot.get("campaign").get("action"), Action.RUN));
//            criteriaQuery.groupBy(requestRoot.get("campaign"));

//            Root<Campaign> campaignRoot = criteriaQuery.from(Campaign.class);
//            Root<Request> requestRoot = criteriaQuery.from(Request.class);
//            Path<BigDecimal> bigDecimalPath = requestRoot.get("campaignCpmRate");
//            criteriaQuery.multiselect(
//                    requestRoot.get("campaign").alias("campaign"),
//                    criteriaBuilder.sum(bigDecimalPath).alias("crt")
//            ).where(criteriaBuilder.equal(campaignRoot.get("action"), Action.RUN));
//            criteriaQuery.groupBy(requestRoot.get("campaign"));

            Root<Campaign> campaignRoot = criteriaQuery.from(Campaign.class);
            Root<Request> requestRoot = criteriaQuery.from(Request.class);

            Join<Campaign, BigDecimal> j = campaignRoot.join("campaignCpmRate", JoinType.LEFT);
            j.on(criteriaBuilder.equal(campaignRoot.get("action"), Action.RUN));
            criteriaQuery.multiselect(campaignRoot, j);

            TypedQuery<Tuple> query = session.createQuery(criteriaQuery);
            List<Tuple> result = query.getResultList();
            transaction.commit();
            return result;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    public static List<Tuple> getListCostRequestTodayByCompanyJpql() throws DbException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            Session session = DbAssistant.getSessionFactory().getCurrentSession();
            List<Tuple> result;
            BigDecimal k_crt = BigDecimal.valueOf(0.0);
            do {
                result = getCompanyForDisplay(session, -1L);
                if (!result.isEmpty()){
                    Tuple tuple = result.get(0);
                    k_crt = tuple.get("k_crt", BigDecimal.class);
                    if (k_crt.compareTo(BigDecimal.valueOf(0.0)) < 0){
                        tuple.get("campaign", Campaign.class).setAction(Action.PAUSE);
                    }
                }
            } while (!result.isEmpty() && k_crt.compareTo(BigDecimal.valueOf(0.0)) < 0);
            transaction.commit();
            return result;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    private static List<Tuple> getCompanyForDisplay(Session session, long sessionId){
        TypedQuery<Tuple> query = session.createQuery(
            "select c as campaign, " +
                "coalesce(sum(r.campaignCpmRate), 0) as crt, " +
                "case when count(r2.id) > 0 then 1 " +
                    "else 0 end as rbbu, " +
                "2 - (case when count(r2.id) > 0 then 1 else 0 end) - (coalesce(sum(r.campaignCpmRate), 0)) / c.dailyBudget / 1000 as k, " +
                "1 - ((coalesce(sum(r.campaignCpmRate), 0)) + c.cpmRate) / c.dailyBudget / 1000 as k_crt " +
                "from Campaign c " +
                "left join Request r on c = r.campaign " +
                    "and r.creationTime between :from and :to " +
                "left join Request r2 on c = r2.campaign " +
                    "and r2.session.id = :sessionId " +
                    "and r2.creationTime between :from and :to " +
                "where c.action = entity.users.Action.RUN " +
                "group by c " +
                "order by k desc ",
                Tuple.class
        ).setMaxResults(1);
        LocalDateTime now = LocalDateTime.now();
//            LocalDateTime from = now.with(LocalTime.MIN);
//            LocalDateTime to = now.with(LocalTime.MAX);
        LocalDateTime from = LocalDateTime.of(2019, Month.SEPTEMBER, 20, 10, 26, 40);
        LocalDateTime to = LocalDateTime.of(2019, Month.SEPTEMBER, 20, 23, 26, 40, 600000000);
        query.setParameter("from", from);
        query.setParameter("to", to);
        query.setParameter("sessionId", sessionId);
        List<Tuple> result = query.getResultList();
        if (!result.isEmpty()){
            Tuple tuple = result.get(0);
            Campaign campaign = tuple.get("campaign", Campaign.class);
            Hibernate.initialize(campaign.getPictures());
        }
        return result;
    }

    public static List<Result> getResultJpql() throws DbException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            Session session = DbAssistant.getSessionFactory().getCurrentSession();
            TypedQuery<Result> query = session.createQuery(
                    "select new main.Result (c, " +
                            "case when sum(r.campaignCpmRate) is null then :nullBigDecimal " +
                                "else sum(r.campaignCpmRate) end, " +
                            "case when count(r2.id) > 0 then 1 " +
                                "else 0 end) " +
                        "from entity.users.customer.Campaign c " +
                        "left join entity.statistics.Request r on c = r.campaign " +
                            "and r.creationTime between :from and :to " +
                        "left join entity.statistics.Request r2 on c = r2.campaign " +
                            "and r2.session.id = :sessionId " +
                            "and r2.creationTime between :from and :to " +
                        "where c.action = 'RUN' " +
                        "group by c "
                            ,
                    Result.class
            );
            query.setParameter("nullBigDecimal", BigDecimal.valueOf(0));
            LocalDateTime now = LocalDateTime.now();
//            LocalDateTime from = now.with(LocalTime.MIN);
//            LocalDateTime to = now.with(LocalTime.MAX);
            LocalDateTime from = LocalDateTime.of(2019, Month.SEPTEMBER, 20, 10, 26, 40);
            LocalDateTime to = LocalDateTime.of(2019, Month.SEPTEMBER, 20, 23, 26, 40, 600000000);
            query.setParameter("from", from)
                .setParameter("to", to)
                .setParameter("sessionId", 1L);
            List<Result> result = query.getResultList();
            transaction.commit();
            return result;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    public static List<Tuple> getListCostRequestTodayByCompanyNative() throws DbException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            Session session = DbAssistant.getSessionFactory().getCurrentSession();
            LocalDateTime now = LocalDateTime.now();
//            LocalDateTime from = now.with(LocalTime.MIN);
//            LocalDateTime to = now.with(LocalTime.MAX);
            LocalDateTime from = LocalDateTime.of(2019, Month.SEPTEMBER, 20, 0, 0, 0);
            LocalDateTime to = LocalDateTime.of(2019, Month.SEPTEMBER, 20, 23, 59, 59, 999999999);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS");
            Long sessionId = 1L;
            String sql =
                "SELECT c.ID id,\n" +
                "       c.DAILY_BUDGET db,\n" +
                "       c.CPM_RATE cpm,\n" +
                "       COALESCE(SUM(r.CAMPAIGN_CPM_RATE),0) crt,\n" +
                "       c.ACTION action,\n" +
                "       c.STATUS status,\n" +
                "       (count(r2.ID) > 0) rbbu,\n" +
                "       2-(count(r2.ID) > 0)-COALESCE(SUM(r.CAMPAIGN_CPM_RATE),0)/c.DAILY_BUDGET/1000 k,\n" +
                "       1-(COALESCE(SUM(r.CAMPAIGN_CPM_RATE),0)+c.CPM_RATE)/c.DAILY_BUDGET/1000 k_crt,\n" +
                "       p.PICTURE_FORMAT_ID pid,\n" +
                "       p.FILENAME fname\n" +
                "FROM CAMPAIGNS c\n" +
                "  LEFT JOIN REQUESTS r\n" +
                "    ON c.ID = r.CAMPAIGN_ID\n" +
                "       AND r.CREATION_TIME BETWEEN '2019-09-20 00:00:00.0000000' AND '2019-09-20 23:59:59.9999999'\n" +
                "  LEFT JOIN REQUESTS r2\n" +
                "    ON c.ID = r2.CAMPAIGN_ID\n" +
                "       AND r2.SESSION_ID = 3\n" +
                "       AND r2.CREATION_TIME BETWEEN '2019-09-20 00:00:00.0000000' AND '2019-09-20 23:59:59.9999999'\n" +
                "  LEFT JOIN PICTURES p\n" +
                "    ON c.ID = p.CAMPAIGN_ID\n" +
                "WHERE c.ACTION = 'RUN'\n" +
                "  AND p.PICTURE_FORMAT_ID = 3\n" +
                "GROUP BY c.ID\n" +
                "ORDER BY k DESC\n" +
                "LIMIT 1";
            TypedQuery<Tuple> query = session.createNativeQuery(sql, Tuple.class);
            List<Tuple> result = query.getResultList();
            transaction.commit();
            return result;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }
}
