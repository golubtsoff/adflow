package service;

import dao.DaoFactory;
import dao.DbAssistant;
import entity.statistics.Options;
import entity.statistics.Request;
import entity.statistics.Session;
import entity.statistics.Viewer;
import entity.users.Action;
import entity.users.customer.Campaign;
import entity.users.customer.Customer;
import entity.users.partner.Platform;
import exception.BadRequestException;
import exception.ConflictException;
import exception.DbException;
import exception.NotFoundException;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import rest.statistics.RequestResource;

import javax.persistence.NoResultException;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

public class RequestService {

    private static final long NO_SESSION = -1L;

    public static Request create(long platformId, Viewer viewer)
            throws NotFoundException, DbException, BadRequestException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            Platform platform = DaoFactory.getPlatformDao().get(platformId);
            if (platform == null){
                DbAssistant.transactionRollback(transaction);
                throw new BadRequestException("Platform with id = " + platformId + " not existed");
            }

            Campaign campaign = getNextCampaignNoSession(platform);
            if (campaign == null){
                transaction.commit();
                throw new NotFoundException();
            }

            Session session = new Session(platform, viewer);
            DaoFactory.getSessionDao().create(session);

            Request request = session.getRequestInstance(campaign, getDurationShow());

            DaoFactory.getSessionDao().update(session);
            DaoFactory.getRequestDao().create(request);

            transaction.commit();
            return request;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    // TODO: delete getRandomCampaign()
    private static Campaign getNextCampaignNoSession(Platform platform){
        return getCampaignForDisplay(platform, NO_SESSION);
    }

    private static Campaign getCampaignForDisplay(Platform platform, long sessionId){
        org.hibernate.Session session = DbAssistant.getSessionFactory().getCurrentSession();
        List<Tuple> result;
        Integer balanceIsOver = 0;
        Integer dailyBudgetIsOver = 0;
        Integer rbbu = 0;
        do {
            result = getDataForCampaignApproval(session, platform, sessionId);
            if (!result.isEmpty()){
                Tuple tuple = result.get(0);
                balanceIsOver = tuple.get("balance_is_over", Integer.class);
                dailyBudgetIsOver = tuple.get("db_is_over", Integer.class);
//                rbbu = tuple.get("rbbu", BigInteger.class).intValue();
                rbbu = tuple.get("rbbu", Integer.class);
                if (dailyBudgetIsOver == 1 || balanceIsOver == 1){
                    long campaignId = tuple.get("id", BigInteger.class).longValue();
                    Campaign campaign = DaoFactory.getCampaignDao().get(campaignId);
                    campaign.setAction(Action.PAUSE);
                }
            }
        } while (!result.isEmpty() && (dailyBudgetIsOver == 1 || balanceIsOver == 1));
        if (!result.isEmpty()){
            long campaignId = result.get(0).get("id", BigInteger.class).longValue();
            Campaign campaign = DaoFactory.getCampaignDao().get(campaignId);
            if (rbbu == 0){
                changeBalanceOfCustomer(campaign);
            }
            return campaign;
        } else {
            return null;
        }
    }

    private static void changeBalanceOfCustomer(Campaign campaign){
        Customer customer = campaign.getCustomer();
        BigDecimal newBalance = customer.getAccount().getBalance()
                .subtract(
                        campaign.getCpmRate()
                                .divide(
                                        BigDecimal.valueOf(1000),
                                        4,
                                        BigDecimal.ROUND_HALF_UP)
                );
        campaign.getCustomer().getAccount().setBalance(newBalance);
    }

    private static List<Tuple> getDataForCampaignApproval(
            org.hibernate.Session session,
            Platform platform,
            long sessionId){
        LocalDateTime now = LocalDateTime.now();
            LocalDateTime from = now.with(LocalTime.MIN);
            LocalDateTime to = now.with(LocalTime.MAX);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS");
        String sql =
                "SELECT\n" +
                "  c.ID                                                                                   id,\n" +
                "  c.DAILY_BUDGET                                                                         db,\n" +
                "  c.CPM_RATE                                                                             campaign_cpm,\n" +
                "  COALESCE(SUM(r.CAMPAIGN_CPM_RATE), 0) / 1000                                           crt,\n" +
                "  c.ACTION                                                                               action,\n" +
                "  c.STATUS                                                                               status,\n" +
                "  (select count(r2.id) > 0 from requests r2 where\n" +
                        "    c.ID = r2.CAMPAIGN_ID\n" +
                        "    AND r2.SESSION_ID = " + sessionId + " \n" +
                        "    AND r2.CREATION_TIME BETWEEN '" + from.format(formatter) + "' \n" +
                        "    AND '" + to.format(formatter) + "') rbbu,\n" +
                "  2 - (select count(r2.id) > 0 from requests r2 where\n" +
                        "    c.ID = r2.CAMPAIGN_ID\n" +
                        "    AND r2.SESSION_ID = " + sessionId + " \n" +
                        "    AND r2.CREATION_TIME BETWEEN '" + from.format(formatter) + "' \n" +
                        "    AND '" + to.format(formatter) + "') \n" +
                        "- COALESCE(SUM(r.CAMPAIGN_CPM_RATE), 0) / c.DAILY_BUDGET / 1000 k,\n" +
                "  c.DAILY_BUDGET < ((COALESCE(SUM(r.CAMPAIGN_CPM_RATE), 0) + c.CPM_RATE) / 1000)         db_is_over,\n" +
                "  cu.BALANCE < (c.CPM_RATE / 1000)                                                       balance_is_over,\n" +
                "  p.PICTURE_FORMAT_ID                                                                    pid,\n" +
                "  p.FILENAME                                                                             fname,\n" +
                "  pl.CPM_RATE                                                                            platform_cpm,\n" +
                "  cu.BALANCE                                                                             balance\n" +
                "FROM CAMPAIGNS c\n" +
                "LEFT JOIN (select *, row_number() over (partition by r3.SESSION_ID, r3.CAMPAIGN_ID ORDER BY id) rn from requests r3) r\n" +
                "  ON c.ID = r.CAMPAIGN_ID\n" +
                "    AND r.CREATION_TIME BETWEEN '" + from.format(formatter) + "' AND '" + to.format(formatter) + "'\n" +
                "    AND r.rn = 1\n" +
                "LEFT JOIN PICTURES p\n" +
                "  ON c.ID = p.CAMPAIGN_ID\n" +
                "LEFT JOIN PLATFORMS pl\n" +
                "  ON pl.ID = " + platform.getId() + " \n" +
                "LEFT JOIN CUSTOMERS cu\n" +
                "  ON cu.ID = c.CUSTOMER_ID\n" +
                "WHERE c.ACTION = 'RUN'\n" +
                "     AND p.PICTURE_FORMAT_ID = pl.PICTURE_FORMAT_ID \n" +
                "     AND c.CPM_RATE >= pl.CPM_RATE\n" +
                "GROUP BY c.ID\n" +
                "ORDER BY k DESC\n" +
                "LIMIT 1";
        TypedQuery<Tuple> query = session.createNativeQuery(sql, Tuple.class);
        return query.getResultList();
    }

    private static Campaign getRandomCampaign(){
        List<Campaign> campaigns = DaoFactory.getCampaignDao().getAllByAction(Action.RUN);
        Random random = new Random();
        int number = random.nextInt(campaigns.size());
        if (number > 0 && number % 4 == 0){
            --number;
        }
        return campaigns.get(number);
    }

    // TODO: delete getRandomCampaign()
    private static Campaign getNextCampaignWithSession(Platform platform, Session session){
        return getCampaignForDisplay(platform, session.getId());
    }

    private static int getDurationShow(){
        return DaoFactory.getOptionsDao().getAll().get(0).getDurationShow();
    }

    public static Request create(long platformId, long sessionId)
            throws DbException, NotFoundException, BadRequestException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            Platform platform = DaoFactory.getPlatformDao().get(platformId);
            Session session = DaoFactory.getSessionDao().get(sessionId);
            if (platform == null || session == null || !session.getPlatform().equals(platform)){
                DbAssistant.transactionRollback(transaction);
                throw new BadRequestException();
            }
            Campaign campaign = getNextCampaignWithSession(platform, session);
            if (campaign == null){
                transaction.commit();
                throw new NotFoundException();
            }

            Request request = session.getRequestInstance(campaign, getDurationShow());
            DaoFactory.getSessionDao().update(session);
            DaoFactory.getRequestDao().create(request);

            transaction.commit();
            return request;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

//    TODO: add decreasing balance of the customer
    public static void update(
            long platformId,
            long requestId,
            @NotNull RequestResource.UpdateRequestDto updateRequestDto
    ) throws BadRequestException, DbException, ConflictException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            Request request = DaoFactory.getRequestDao().get(requestId);
            if (request == null || request.getSession().getPlatform().getId() != platformId){
                DbAssistant.transactionRollback(transaction);
                throw new BadRequestException();
            }

            if (LocalDateTime.now().minusSeconds(request.getDurationShow()).isAfter(request.getCreationTime())){
                DbAssistant.transactionRollback(transaction);
                throw new ConflictException();
            }

            request.updateRequest(updateRequestDto.isClickOn());
            DaoFactory.getRequestDao().update(request);
            DaoFactory.getSessionDao().update(request.getSession());

            transaction.commit();
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }
}
