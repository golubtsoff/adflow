package main;

import dao.DaoFactory;
import dao.DbAssistant;
import entity.statistics.Request;
import entity.statistics.Viewer;
import entity.users.*;
import entity.users.customer.Campaign;
import entity.users.customer.Customer;
import entity.users.customer.Picture;
import entity.users.partner.Partner;
import entity.users.partner.Platform;
import entity.users.partner.PlatformToken;
import entity.users.user.Role;
import entity.users.user.User;
import exception.*;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import rest.statistics.RequestResource;
import service.*;

import javax.persistence.NoResultException;
import javax.persistence.Tuple;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

public class Main {

    public static void main(String[] args)
            throws Exception {
//        initData();
//        updateStatusUser();
//        List<Campaign> campaigns = QueryTest.getCampaigns();
        checkRequestService();

        DbAssistant.close();
//        testScheduler();
    }

    public static void checkRequestService() throws BadRequestException, NotFoundException, DbException {
        Request request = RequestService.create(1, new Viewer("Вася", createIp()));
        for (int i = 0; i < 10; ++i){
            request = RequestService.create(1, request.getSession().getId());
        }
    }

    public static void updateStatusUser() throws DbException {
        List<User> users = UserService.getByStatus(Status.WORKING);
        if (!users.isEmpty()){
            User user = UserService.getByStatus(Status.WORKING).get(0);
            user.setStatus(Status.REMOVED);
            UserService.update(user);
        }
    }

    public static void testScheduler() throws SchedulerException {

        // Извлекаем планировщик из schedule factory
        Scheduler scheduler =  StdSchedulerFactory.getDefaultScheduler();

        // define the job and tie it to our HelloJob class
        JobDetail job = JobBuilder.newJob(SimpleQuartzJob.class)
                .withIdentity("job1", "group1")
                .build();

        // Trigger the job to run now, and then repeat every 40 seconds
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger1", "group1")
                .startNow()
                .withSchedule(simpleSchedule()
                        .withIntervalInSeconds(40)
                        .repeatForever())
                .build();

        // Tell quartz to schedule the job using our trigger
        scheduler.scheduleJob(job, trigger);

        // and start it off
        scheduler.start();
//        scheduler.shutdown();
    }

    public static void initData() throws Exception {
        List<User> customers = createUsers(5, Role.CUSTOMER);
        List<User> partners = createUsers(6, Role.PARTNER);
        List<User> admins = createUsers(2, Role.ADMIN);
        List<PictureFormat> formats = createPictureFormats(3);
        List<Campaign> campaigns = createCampaigns(customers, 10, formats);
        List<Platform> platforms = createPlatforms(partners, 5, formats);

        List<Viewer> viewers = createViewers(100);
        List<Request> requests = createRequests(platforms, viewers, 100, 20);
    }

    private static List<User> createUsers(int quantity, Role role) throws DbException, ServiceException {
        List<User> users = new ArrayList<>();
        User user;
        for (int i = 0; i < quantity; i++){
            user = UserService.signUp(
                role.toString().toLowerCase() + "_" + String.valueOf(i),
                "123",
                role);
            assert user != null;
            user.setStatus(Status.WORKING);
            UserService.update(user);
            users.add(user);
        }
        return users;
    }

    private static List<Platform> createPlatforms(List<User> users, int maxQuantity, List<PictureFormat> formats)
            throws Exception {
        List<Platform> platforms = new ArrayList<>();
        Random rnd = new Random();
        for (User user : users){
            int quantity = rnd.nextInt(maxQuantity) + 1;
            for (int i = 0; i < quantity; i++){
                PictureFormat pictureFormat = formats.get(rnd.nextInt(formats.size()));
                Platform platform = new Platform(
                        null,
                        "Platform_" + i,
                        "Title of Platform_" + i,
                        BigDecimal.valueOf(800 + rnd.nextInt(400)),
                        pictureFormat,
                        Action.RUN,
                        Status.WORKING
                );
                createPlatform(user, platform);
                platforms.add(platform);
            }
        }
        return platforms;
    }

    private static Platform createPlatform(User userPartner, Platform platform) throws Exception {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            Partner partner = DaoFactory.getPartnerDao().getByUserId(userPartner.getId());
            platform.setPartner(partner);

            DaoFactory.getPlatformDao().create(platform);
            PlatformToken platformToken = new PlatformToken(platform);
            DaoFactory.getPlatformTokenDao().create(platformToken);

            transaction.commit();
            return platform;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    private static List<Campaign> createCampaigns(List<User> users, int maxQuantity, List<PictureFormat> formats)
            throws DbException {
        List<Campaign> campaigns = new ArrayList<>();
        Random rnd = new Random();
        for (User user : users){
            int quantity = rnd.nextInt(maxQuantity) + 1;
            Action[] actions = Action.values();
            for (int i = 0; i < quantity; i++){
                List<Picture> pictures = createPictures(user, i, formats);
                Campaign campaign = new Campaign(
                        null,
                        "Campaign_" + i,
                        "Title of Campaign_" + i,
                        "http://somesite.com",
                        BigDecimal.valueOf(100 + rnd.nextInt(50)),
                        BigDecimal.valueOf(1000 + rnd.nextInt(1000)),
                        actions[rnd.nextInt(actions.length)],
                        Status.WORKING
                );
                createCampaign(user, pictures, campaign);
                campaigns.add(campaign);
            }
        }
        return campaigns;
    }

    private static Campaign createCampaign(
            User userCustomer,
            List<Picture> pictures,
            Campaign campaign
    ) throws DbException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            Customer customer = DaoFactory.getCustomerDao().getByUserId(userCustomer.getId());
            campaign.setCustomer(customer);

            for (Picture picture : pictures){
                campaign.addPicture(picture);
            }
            DaoFactory.getCampaignDao().create(campaign);

            transaction.commit();
            return campaign;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    private static List<Picture> createPictures(User user, int numCampaignByUser, List<PictureFormat> formats){
        List<Picture> pictures = new ArrayList<>();
        Random rnd = new Random();
        int quantity = rnd.nextInt(formats.size()) + 1;
        for (int i = 0; i < quantity; i++){
            Picture picture = new Picture(
                    "picture_"
                            + user.getId()
                            + "_"
                            + numCampaignByUser
                            + "_"
                            + i, formats.get(i));
            pictures.add(picture);
        }
        return pictures;
    }

    private static PictureFormat createPictureFormat(PictureFormat pictureFormat) throws DbException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            DaoFactory.getPictureFormatDao().create(pictureFormat);
            transaction.commit();
            return pictureFormat;
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    private static List<PictureFormat> createPictureFormats(int quantity) throws DbException {
        List<PictureFormat> formats = new ArrayList<>();
        int width = 200;
        int height = 50;
        for (int i = 0; i < quantity; i++){
            PictureFormat pictureFormat = new PictureFormat(width,height);
            createPictureFormat(pictureFormat);
            formats.add(pictureFormat);
            width += 5;
            height += 5;
        }
        return formats;
    }

    private static List<Viewer> createViewers(int quantity){
        List<Viewer> viewers = new ArrayList<>();
        Random rnd = new Random();
        for (int i = 0; i < quantity; i++){
            Viewer viewer = new Viewer("Name_" + rnd.nextInt(100000), createIp());
            viewers.add(viewer);
        }
        return viewers;
    }

    private static String createIp(){
        Random rnd = new Random();
        return "" + String.valueOf(rnd.nextInt(256 - 10) + 10) + "."
                + rnd.nextInt(256) + "."
                + rnd.nextInt(256) + "."
                + rnd.nextInt(256);
    }

    private static List<Request> createRequests(
            List<Platform> platforms,
            List<Viewer> viewers,
            int quantitySession,
            int maxQuantityRequestBySession
    )
            throws BadRequestException, NotFoundException, DbException, ConflictException {
        List<Request> requests = new ArrayList<>();
        Random rnd = new Random();
        RequestResource requestResource = new RequestResource();
        for (int i = 0; i < quantitySession; i++){
            Platform platform = platforms.get(rnd.nextInt(platforms.size()));
            Viewer viewer = viewers.get(rnd.nextInt(viewers.size()));
            Request request = RequestService.create(platform.getId(), viewer);
            RequestService.update(platform.getId(), request.getId(), getUpdateRequestDto(requestResource));
            requests.add(request);

            int quantityRequestBySession = rnd.nextInt(maxQuantityRequestBySession-1);
            for (int j = 0; j < quantityRequestBySession; j++){
                Request request1 = RequestService.create(platform.getId(), request.getSession().getId());
                if (j < quantityRequestBySession - 1){
                    RequestService.update(platform.getId(), request1.getId(), getUpdateRequestDto(requestResource));
                } else if (rnd.nextBoolean()){
                    RequestService.update(platform.getId(), request1.getId(), getUpdateRequestDto(requestResource));
                }
                requests.add(request1);
            }
        }
        return requests;
    }

    private static RequestResource.UpdateRequestDto getUpdateRequestDto(RequestResource requestResource){
        RequestResource.UpdateRequestDto updateRequestDto = requestResource.new UpdateRequestDto();
        Random rnd = new Random();
        updateRequestDto.setClickOn(rnd.nextBoolean());
        return updateRequestDto;
    }

}
