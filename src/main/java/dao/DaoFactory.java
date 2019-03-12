package dao;

import entity.*;
import entity.campaign.Campaign;
import entity.partner.AdvertisingPlatform;
import entity.partner.Partner;
import entity.session.Session;
import entity.session.Viewer;
import entity.user.Contact;
import entity.user.Person;
import entity.user.Token;
import entity.user.User;

import java.util.Arrays;
import java.util.List;

public abstract class DaoFactory {

    private static final List<Dao<?>> daoList = Arrays.asList(
            new DaoImpl<>(Account.class),
            new DaoImpl<>(Administrator.class),
            new DaoImpl<>(AdvertisingPlatform.class),
            new DaoImpl<>(Campaign.class),
            new DaoImpl<>(Contact.class),
            new DaoImpl<>(Customer.class),
            new DaoImpl<>(Partner.class),
            new DaoImpl<>(Person.class),
            new DaoImpl<>(Picture.class),
            new DaoImpl<>(PictureFormat.class),
            new DaoImpl<>(Session.class),
            new DaoImpl<>(Token.class),
            new UserDao(),
            new DaoImpl<>(Viewer.class)
    );

    public static <T> Dao getDao(Class<T> cl){
        for(Dao dao : daoList){
            if (cl.isInstance(dao.getParameterizedClass()))
                return dao;
        }
        return null;
    }

    public static UserDao getUserDao(){
        return (UserDao) getDao(User.class);
    }
}