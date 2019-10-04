package service;

import dao.DaoFactory;
import dao.DbAssistant;
import entity.statistics.Options;
import entity.statistics.Request;
import entity.statistics.Session;
import entity.statistics.Viewer;
import entity.users.Action;
import entity.users.customer.Campaign;
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
                throw new BadRequestException();
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
//        return getRandomCampaign();
        return getCampaignForDisplay(platform, NO_SESSION);
    }

    private static Campaign getCampaignForDisplay(Platform platform, long sessionId){
        org.hibernate.Session session = DbAssistant.getSessionFactory().getCurrentSession();
        List<Tuple> result;
        BigDecimal k_crt = BigDecimal.valueOf(0.0);
        do {
            result = getDataForCampaignApproval(session, platform, sessionId);
            if (!result.isEmpty()){
                Tuple tuple = result.get(0);
                k_crt = tuple.get("k_crt", BigDecimal.class);
                if (k_crt.compareTo(BigDecimal.valueOf(0.0)) < 0){
                    long campaignId = tuple.get("id", BigInteger.class).longValue();
                    Campaign campaign = DaoFactory.getCampaignDao().get(campaignId);
                    campaign.setAction(Action.PAUSE);
                }
            }
        } while (!result.isEmpty() && k_crt.compareTo(BigDecimal.valueOf(0.0)) < 0);
        if (result.isEmpty()){
            return null;
        } else {
            long campaignId = result.get(0).get("id", BigInteger.class).longValue();
            return DaoFactory.getCampaignDao().get(campaignId);
        }
    }

    private static List<Tuple> getDataForCampaignApproval(
            org.hibernate.Session session,
            Platform platform,
            long sessionId){
        LocalDateTime now = LocalDateTime.now();
        // TODO: fix setting from and to
            LocalDateTime from = now.with(LocalTime.MIN);
            LocalDateTime to = now.with(LocalTime.MAX);
//        LocalDateTime from = LocalDateTime.of(2019, Month.SEPTEMBER, 20, 0, 0, 0);
//        LocalDateTime to = LocalDateTime.of(2019, Month.SEPTEMBER, 20, 23, 59, 59, 999999999);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS");
        String sql =
                "SELECT\n" +
                "  c.ID                                                                                   id,\n" +
                "  c.DAILY_BUDGET                                                                         db,\n" +
                "  c.CPM_RATE                                                                             campaign_cpm,\n" +
                "  COALESCE(SUM(r.CAMPAIGN_CPM_RATE), 0)                                                  crt,\n" +
                "  c.ACTION                                                                               action,\n" +
                "  c.STATUS                                                                               status,\n" +
                "  (count(r2.ID) > 0)                                                                     rbbu,\n" +
                "  2 - (count(r2.ID) > 0) - COALESCE(SUM(r.CAMPAIGN_CPM_RATE), 0) / c.DAILY_BUDGET / 1000 k,\n" +
                "  1 - (COALESCE(SUM(r.CAMPAIGN_CPM_RATE), 0) + c.CPM_RATE) / c.DAILY_BUDGET / 1000       k_crt,\n" +
                "  p.PICTURE_FORMAT_ID                                                                    pid,\n" +
                "  p.FILENAME                                                                             fname,\n" +
                "  pl.CPM_RATE                                                                            platform_cpm\n" +
                "FROM CAMPAIGNS c\n" +
                "LEFT JOIN REQUESTS r\n" +
                "  ON c.ID = r.CAMPAIGN_ID\n" +
                "    AND r.CREATION_TIME BETWEEN '" + from.format(formatter) + "' AND '" + to.format(formatter) + "'\n" +
                "LEFT JOIN REQUESTS r2\n" +
                "  ON c.ID = r2.CAMPAIGN_ID\n" +
                "    AND r2.SESSION_ID = " + sessionId + " \n" +
                "    AND r2.CREATION_TIME BETWEEN '" + from.format(formatter) + "' AND '" + to.format(formatter) + "'\n" +
                "LEFT JOIN PICTURES p\n" +
                "  ON c.ID = p.CAMPAIGN_ID\n" +
                "LEFT JOIN PLATFORMS pl\n" +
                "  ON pl.ID = " + platform.getId() + " \n" +
                "WHERE c.ACTION = 'RUN'\n" +
                "     AND p.PICTURE_FORMAT_ID = " + platform.getPictureFormat().getId() + " \n" +
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
//        return getRandomCampaign();
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
