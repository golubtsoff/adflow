package dao;

import entity.statistics.Options;
import entity.statistics.Request;
import entity.users.Administrator;
import entity.users.customer.Campaign;
import entity.users.customer.Customer;
import entity.users.PictureFormat;
import entity.users.partner.Platform;
import entity.users.partner.Partner;
import entity.users.partner.PlatformToken;
import entity.users.user.*;

import exception.ServiceException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.hibernate.service.ServiceRegistry;
import rest.statistics.dto.GroupByElementsStatistics;
import service.OptionsService;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class DbAssistant {
//    validate: validate the schema, makes no changes to the database.
//    update: update the schema.
//    create: creates the schema, destroying previous data.
//    create-drop: drop the schema when the SessionFactory is closed explicitly, typically when the application is stopped.
    private static String hibernate_hbm2ddl_auto = null;

    private static final String path = "/database.properties";
    private static SessionFactory sessionFactory;

    static {
        try {
//            java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.OFF);
            Configuration configuration = getConfiguration();
            sessionFactory = createSessionFactory(configuration);
            initBase();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    private DbAssistant() {
    }

    public static void setTestConfiguration() throws ServiceException {
        close();
        Configuration configuration = getConfiguration();
        configuration.setProperty("hibernate.hbm2ddl.auto", "create");
        sessionFactory = createSessionFactory(configuration);
        initBase();
    }

    public static Transaction getTransaction(){
        Session session = DbAssistant.getSessionFactory().getCurrentSession();
        Transaction transaction = session.getTransaction();
        if (!transaction.isActive()) {
            transaction = session.beginTransaction();
        }
        return transaction;
    }

    public static void transactionRollback(Transaction transaction){
        if (transaction.getStatus() == TransactionStatus.ACTIVE
                || transaction.getStatus() == TransactionStatus.MARKED_ROLLBACK) {
            transaction.rollback();
        }
    }

    public static SessionFactory getSessionFactory(){
        return sessionFactory;
    }

    private static Configuration getConfiguration() throws ServiceException {
        Configuration configuration = new Configuration();
        addAnnotatedClassToConfiguration(configuration);

        try (InputStream is = DbAssistant.class.getResourceAsStream(path)) {
            Properties props = new Properties();
            props.load(is);

            configuration.setProperty("hibernate.dialect", props.getProperty("hibernate.dialect"));
            configuration.setProperty("hibernate.connection.driver_class", props.getProperty("hibernate.connection.driver_class"));
            configuration.setProperty("hibernate.connection.url", props.getProperty("hibernate.connection.url"));
            configuration.setProperty("hibernate.connection.username", props.getProperty("hibernate.connection.username"));
            configuration.setProperty("hibernate.connection.password", props.getProperty("hibernate.connection.password"));
            configuration.setProperty("hibernate.show_sql", props.getProperty("hibernate.show_sql"));
            configuration.setProperty("hibernate.hbm2ddl.auto", props.getProperty("hibernate.hbm2ddl.auto"));
            configuration.setProperty("hibernate.connection.pool_size", props.getProperty("hibernate.connection.pool_size"));
            configuration.setProperty("hibernate.current_session_context_class", "thread");

            hibernate_hbm2ddl_auto = configuration.getProperty("hibernate.hbm2ddl.auto");

        } catch (IOException e) {
            throw new ServiceException("Invalid config file " + path);
        }
        return configuration;
    }

    private static void addAnnotatedClassToConfiguration(Configuration configuration) {
        configuration
                .addAnnotatedClass(Administrator.class)
                .addAnnotatedClass(Platform.class)
                .addAnnotatedClass(Campaign.class)
                .addAnnotatedClass(Customer.class)
                .addAnnotatedClass(Options.class)
                .addAnnotatedClass(Partner.class)
                .addAnnotatedClass(PictureFormat.class)
                .addAnnotatedClass(PlatformToken.class)
                .addAnnotatedClass(Request.class)
                .addAnnotatedClass(entity.statistics.Session.class)
                .addAnnotatedClass(UserToken.class)
                .addAnnotatedClass(User.class)
        ;
    }

    private static SessionFactory createSessionFactory(Configuration configuration) {
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
        builder.applySettings(configuration.getProperties());
        ServiceRegistry serviceRegistry = builder.build();
        return configuration.buildSessionFactory(serviceRegistry);
    }

    public static void close(){
        sessionFactory.close();
    }

    public static boolean isTesting(){
        return hibernate_hbm2ddl_auto.equalsIgnoreCase("create")
                || hibernate_hbm2ddl_auto.equalsIgnoreCase("create-drop");
    }

    private static void initBase() throws ServiceException {
        try{
            OptionsService.initOptions();
        } catch (Exception e){
            throw new ServiceException("Initialization error Options table");
        }
    }
}