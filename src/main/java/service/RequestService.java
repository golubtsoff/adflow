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
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

public class RequestService {

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
                DbAssistant.transactionRollback(transaction);
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
//  TODO: replace random choice
    private static Campaign getNextCampaignNoSession(Platform platform){
        List<Campaign> campaigns = DaoFactory.getCampaignDao().getAllByAction(Action.RUN);
        Random random = new Random();
        int number = random.nextInt(campaigns.size());
        return campaigns.get(number);
    }

    //  TODO: replace random choice
    private static Campaign getNextCampaignWithSession(Platform platform, Session session){
        List<Campaign> campaigns = DaoFactory.getCampaignDao().getAllByAction(Action.RUN);
        Random random = new Random();
        int number = random.nextInt(campaigns.size());
        return campaigns.get(number);
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
                DbAssistant.transactionRollback(transaction);
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

            int durationShow = DaoFactory.getOptionsDao().getAll().get(0).getDurationShow();
            if (LocalDateTime.now().minusSeconds(durationShow).isAfter(request.getCreationTime())){
                DbAssistant.transactionRollback(transaction);
                throw new ConflictException();
            }

            request.updateRequest(updateRequestDto.isClickOn());
            DaoFactory.getRequestDao().update(request);
//            TODO: check if the session is saved without calling the update function
            DaoFactory.getSessionDao().update(request.getSession());

            transaction.commit();
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }
}
