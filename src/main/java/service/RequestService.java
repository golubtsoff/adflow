package service;

import dao.DaoFactory;
import dao.DbAssistant;
import entity.statistics.Request;
import entity.users.customer.Campaign;
import entity.users.customer.Picture;
import entity.users.partner.Platform;
import exception.DbException;
import exception.NotFoundException;
import exception.ServiceException;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import rest.statistics.RequestResource;
import util.NullAware;

import javax.persistence.NoResultException;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

public class RequestService {

    public static String pathToFile = "http://app4pro.ru/pictures/";

    public static RequestResource.InitialResponseDto create(
            long platformId,
            RequestResource.InitialRequestDto initialRequestDto,
            RequestResource.InitialResponseDto initialResponseDto
    ) throws NotFoundException, DbException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            Platform platform = DaoFactory.getPlatformDao().get(platformId);
            Campaign campaign = getNextCampaign();
            if (platform == null || campaign == null){
                throw new NotFoundException();
            }
            String url = getUrlToPicture(platform, campaign);
            String pathOnclick = campaign.getPathOnClick();

            Request request = new Request(platform, campaign);
            DaoFactory.getRequestDao().create(request);
            transaction.commit();

            initialResponseDto.setRequestId(request.getId());
            initialResponseDto.setUrlForLoadFile(url);
            initialResponseDto.setPathOnClick(pathOnclick);
            return initialResponseDto;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    private static Campaign getNextCampaign(){
        return null;
    }

    private static String getUrlToPicture(Platform platform, Campaign campaign){
        Set<Picture> pictures = campaign.getPictures();
        for (Picture picture : pictures){
            if (picture.getPictureFormat() == platform.getPictureFormat()){
                return pathToFile + picture.getFileName();
            }
        }
        return null;
    }

    public static RequestResource.InitialResponseDto create(
            long platformId,
            long sessionId) {
        return null;
    }

    public static void update(
            long platformId,
            long requestId,
            RequestResource.UpdateRequestDto updateRequestDto
    ) {

    }
}
