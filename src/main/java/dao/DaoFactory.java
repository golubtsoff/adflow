package dao;

import dao.impl.*;

import java.util.Arrays;
import java.util.List;

public abstract class DaoFactory {

    private static final List<Dao> daoList = Arrays.asList(
            new AdministratorDao(),
            new AdvertisingPlatformDao(),
            new AdvertisingPlatformStatisticsDao(),
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

    public static AdvertisingPlatformDao getAdvertisingPlatformDao(){
        return getDao(AdvertisingPlatformDao.class);
    }

    public static AdvertisingPlatformStatisticsDao getAdvertisingPlatformStatisticsDao(){
        return getDao(AdvertisingPlatformStatisticsDao.class);
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

    public static UserTokenDao getTokenDao(){
        return getDao(UserTokenDao.class);
    }

    public static UserDao getUserDao(){
        return getDao(UserDao.class);
    }
}