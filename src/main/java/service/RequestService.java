package service;

import dao.DaoFactory;
import dao.DbAssistant;
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
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import rest.statistics.RequestResource;

import javax.persistence.NoResultException;
import javax.persistence.Tuple;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

public abstract class RequestService {

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
            changeBalanceOfCustomer(campaign);

            Hibernate.initialize(request.getCampaign());
            Hibernate.initialize(request.getCampaign().getCustomer());
            Hibernate.initialize(request.getCampaign().getPictures());

            transaction.commit();
            return request;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    private static Campaign getNextCampaignNoSession(Platform platform){
        return getCampaignForDisplay(platform, NO_SESSION);
    }

    private static Campaign getCampaignForDisplay(Platform platform, long sessionId){
        List<Tuple> result;
        Integer balanceIsOver = 0;
        Integer dailyBudgetIsOver = 0;
        Integer rbbu = 0;
        do {
            result = DaoFactory.getRequestDao().getDataForCampaignApproval(platform, sessionId);
            if (!result.isEmpty()){
                Tuple tuple = result.get(0);
                balanceIsOver = tuple.get("balance_is_over", Integer.class);
                dailyBudgetIsOver = tuple.get("db_is_over", Integer.class);
                rbbu = tuple.get("rbbu", Integer.class);
                if (dailyBudgetIsOver == 1 || balanceIsOver == 1){
                    setCampaignOnPause(tuple.get("id", BigInteger.class).longValue());
                }
            }
        } while (!result.isEmpty() && (dailyBudgetIsOver == 1 || balanceIsOver == 1));

        if (!result.isEmpty() && rbbu == 0){
            long campaignId = result.get(0).get("id", BigInteger.class).longValue();
            return DaoFactory.getCampaignDao().get(campaignId);
        }
        return null;
    }

    private static void setCampaignOnPause(long campaignId){
        Campaign campaign = DaoFactory.getCampaignDao().get(campaignId);
        campaign.setAction(Action.PAUSE);
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
            if (session == null || !session.getPlatform().equals(platform)){
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
            changeBalanceOfCustomer(campaign);

            Hibernate.initialize(request.getCampaign());
            Hibernate.initialize(request.getCampaign().getCustomer());
            Hibernate.initialize(request.getCampaign().getPictures());

            transaction.commit();
            return request;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

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

            request.updateRequest(updateRequestDto.isClickOn(), updateRequestDto.getActualShowTime());
            DaoFactory.getRequestDao().update(request);
            DaoFactory.getSessionDao().update(request.getSession());

            transaction.commit();
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }
}
