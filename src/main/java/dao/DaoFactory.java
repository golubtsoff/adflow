package dao;

import entity.users.customer.Campaign;
import entity.users.customer.Customer;
import entity.users.customer.Picture;
import entity.users.customer.PictureFormat;
import entity.users.partner.AdvertisingPlatform;
import entity.users.partner.Partner;
import entity.statistics.Session;
import entity.statistics.Viewer;
import entity.users.Account;
import entity.users.Administrator;
import entity.users.user.Contact;
import entity.users.user.Person;
import entity.users.user.Token;
import entity.users.user.User;

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