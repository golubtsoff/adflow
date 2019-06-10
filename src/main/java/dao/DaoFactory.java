package dao;

import dao.impl.*;

import java.util.Arrays;
import java.util.List;

public abstract class DaoFactory {

    private static final List<Dao> daoList = Arrays.asList(
            new AdministratorDao(),
            new PlatformDao(),
            new PlatformStatisticsDao(),
            new CampaignDao(),
            new CampaignStatisticsDao(),
            new CustomerDao(),
            new PartnerDao(),
            new PictureFormatDao(),
            new PlatformTokenDao(),
            new RequestDao(),
            new SessionDao(),
            new UserTokenDao(),
            new UserDao()
    );

    private static <T extends Dao> T getDao(Class<T> cl){
        for(Dao dao : daoList){
            if (cl.isInstance(dao))
                return cl.cast(dao);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> Dao<T> getDaoByParametrizedClass(Class<T> cl){
        for(Dao dao : daoList){
            if (cl == dao.getParameterizedClass())
                return dao;
        }
        return null;
    }

    public static AdministratorDao getAdministratorDao(){
        return getDao(AdministratorDao.class);
    }

    public static PlatformDao getPlatformDao(){
        return getDao(PlatformDao.class);
    }

    public static PlatformStatisticsDao getPlatformStatisticsDao(){
        return getDao(PlatformStatisticsDao.class);
    }

    public static CampaignDao getCampaignDao(){
        return getDao(CampaignDao.class);
    }

    public static CampaignStatisticsDao getCampaignStatisticsDao(){
        return getDao(CampaignStatisticsDao.class);
    }

    public static CustomerDao getCustomerDao(){
        return getDao(CustomerDao.class);
    }

    public static PartnerDao getPartnerDao(){
        return getDao(PartnerDao.class);
    }

    public static PictureFormatDao getPictureFormatDao(){
        return getDao(PictureFormatDao.class);
    }

    public static PlatformTokenDao getPlatformTokenDao(){
        return getDao(PlatformTokenDao.class);
    }

    public static RequestDao getRequestDao(){
        return getDao(RequestDao.class);
    }

    public static SessionDao getSessionDao(){
        return getDao(SessionDao.class);
    }

    public static UserTokenDao getUserTokenDao(){
        return getDao(UserTokenDao.class);
    }

    public static UserDao getUserDao(){
        return getDao(UserDao.class);
    }
}