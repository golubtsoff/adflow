package main;

import dao.DaoFactory;
import dao.DbAssistant;
import entity.statistics.Request;
import entity.statistics.Viewer;
import entity.users.*;
import entity.users.customer.Customer;
import entity.users.partner.Platform;
import entity.users.user.User;
import exception.*;
import org.apache.commons.io.FileUtils;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import rest.statistics.dto.GroupByElementsStatistics;
import service.*;

import javax.persistence.NoResultException;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

public class Main {

    public static void main(String[] args)
            throws Exception {
        testGetGroupByCampaignsStatistics();
//        updateStatusUser();
//        List<Campaign> campaigns = QueryTest.getCampaigns();
//        checkRequestService();
//        checkRequestServiceConcurrent();
//        QueryTest.testPictureFormatDao();
//        CampaignService.updateCampaignActionPauseToRun();
//        testGetCustomer();
        DbAssistant.close();
//        testScheduler();
    }

    public static void testGetGroupByCampaignsStatistics() throws DbException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            List<GroupByElementsStatistics> list = DaoFactory.getRequestDao()
                    .getGroupByCampaignsStatistics(
                            2,
                            LocalDate.of(2020, 1, 11),
                            LocalDate.of(2020, 1, 12)
                    );
            transaction.commit();
            System.out.println(list);
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    public static void testGetCustomer() throws DbException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
//            Customer customer = DaoFactory.getCustomerDao().getByUserId(19L);
            Customer customer = getCustomer(19L);
            transaction.commit();
            System.out.println(customer);
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    private static Customer getCustomer(long user_id){
        return DaoFactory.getCustomerDao().getByUserId(19L);
    }

    public static void checkRequestServiceConcurrent() throws BadRequestException, DbException {
        AtomicInteger countTask = new AtomicInteger();
        int countThread = 1;
        Runnable task = () -> {
            int taskNumber = countTask.incrementAndGet();
            int countNotFoundException = 0;
            int countBadRequestException = 0;
            int countDbException = 0;
            int countException = 0;
            for (int i = 0; i < 30; ++i){
                try{
                    checkRequestService();
                } catch (NotFoundException e){
                    System.out.println("Task " + taskNumber + ": NotFoundException " + i);
                    ++countNotFoundException;
                } catch (BadRequestException e){
                    System.out.println("Task " + taskNumber + ": BadRequestException " + i);
                    ++countBadRequestException;
                    e.printStackTrace();
                } catch (DbException e){
                    System.out.println("Task " + taskNumber + ": DbException " + i);
                    ++countDbException;
                    e.printStackTrace();
                } catch (Exception e){
                    System.out.println("Task " + taskNumber + ": Exception " + i);
                    ++countException;
                    e.printStackTrace();
                }
            }
            System.out.println("task "+ taskNumber + " finished:\n"
                + "NotFoundException: " + countNotFoundException + "\n"
                + "BadRequestException: " + countBadRequestException + "\n"
                + "DbException: " + countDbException + "\n"
                + "Exception: " + countException + "\n"
            );
        };

        for (int i = 0; i < countThread; i++){
            Thread thread = new Thread(task);
            thread.start();
        }
    }

    public static void checkRequestService()
            throws BadRequestException, NotFoundException, DbException, InterruptedException {
        List<Platform> platforms = PlatformService.getAll();
        Random random = new Random();
        int platformId = random.nextInt(platforms.size()) + 1;
        Request request = RequestService.create(platformId, new Viewer(generateName(), createIp()));
        for (int i = 0; i < 10; ++i){
            request = RequestService.create(platformId, request.getSession().getId());
        }
    }

    public static String generateName()
    {
        String characters = "абвгдеёжзийклмнопрстуфхцчшщьыъэюя";
        Random random = new Random();
        int length = 5 + random.nextInt(5);
        char[] text = new char[length];
        for (int i = 0; i < length; i++)
        {
            text[i] = characters.charAt(random.nextInt(characters.length()));
        }
        return new String(text);
    }

    private static String createIp(){
        Random rnd = new Random();
        return "" + String.valueOf(rnd.nextInt(256 - 10) + 10) + "."
                + rnd.nextInt(256) + "."
                + rnd.nextInt(256) + "."
                + rnd.nextInt(256);
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
                        .withIntervalInSeconds(3)
                        .repeatForever())
                .build();

        // Tell quartz to schedule the job using our trigger
        scheduler.scheduleJob(job, trigger);

        // and start it off
        scheduler.start();
//        scheduler.shutdown();
    }



}
