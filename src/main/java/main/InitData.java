package main;

import dao.DaoFactory;
import dao.DbAssistant;
import entity.statistics.Request;
import entity.statistics.Viewer;
import entity.users.Account;
import entity.users.Action;
import entity.users.PictureFormat;
import entity.users.Status;
import entity.users.customer.Campaign;
import entity.users.customer.Customer;
import entity.users.customer.Picture;
import entity.users.partner.Partner;
import entity.users.partner.Platform;
import entity.users.partner.PlatformToken;
import entity.users.user.Role;
import entity.users.user.User;
import exception.*;
import org.apache.commons.io.FileUtils;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import rest.statistics.RequestResource;
import service.AccountService;
import service.RequestService;
import service.UserService;
import util.Links;

import javax.imageio.ImageIO;
import javax.persistence.NoResultException;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class InitData {

    public static final String PATH_TO_UPLOAD_FOLDER = "C:\\apache-tomcat-8.5.16\\webapps\\uploads";

    public static void main(String[] args) throws Exception {
        DbAssistant.setTestConfiguration();
        init();
        DbAssistant.close();
    }

    public static void init() throws Exception {
        cleanUploadFolder();
        List<User> customers = createUsers(5, Role.CUSTOMER);
        updateAccountOfCustomers(customers);
        List<User> partners = createUsers(6, Role.PARTNER);
        List<User> admins = createUsers(2, Role.ADMIN);
        List<PictureFormat> formats = createPictureFormats(
                new PictureFormat(611, 120),
                new PictureFormat(813, 120)
        );
        List<Campaign> campaigns = createCampaigns(customers, 10, formats);
        List<Platform> platforms = createPlatforms(partners, 5, formats);

        List<Viewer> viewers = createViewers(100);
        List<Request> requests = createRequests(platforms, viewers, 100, 20);
    }

    private static void cleanUploadFolder() throws IOException {
        FileUtils.cleanDirectory(new File(PATH_TO_UPLOAD_FOLDER));
    }

    private static List<User> createUsers(int quantity, Role role) throws DbException, ServiceException {
        List<User> users = new ArrayList<>();
        User user;
        for (int i = 0; i < quantity; i++){
            user = UserService.signUp(
                    role.toString().toLowerCase() + "_" + i,
                    "123",
                    role);
            assert user != null;
            user.setStatus(Status.WORKING);
            UserService.update(user);
            users.add(user);
        }
        return users;
    }

    private static void updateAccountOfCustomers(List<User> customers)
            throws NotFoundException, DbException {
        Random rnd = new Random();
        for(User user : customers){
            Account account = AccountService.get(user.getId());
            account.setBalance(BigDecimal.valueOf(500 + rnd.nextInt(1000)));
            AccountService.update(user.getId(), account);
        }
    }

    private static List<PictureFormat> createPictureFormats(PictureFormat... pictureFormatArray) throws DbException {
        List<PictureFormat> formats = new ArrayList<>();
        for (PictureFormat pictureFormat : pictureFormatArray){
            createPictureFormat(pictureFormat);
            formats.add(pictureFormat);
        }
        return formats;
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

    private static List<Campaign> createCampaigns(List<User> users, int maxQuantity, List<PictureFormat> formats)
            throws DbException {
        List<Campaign> campaigns = new ArrayList<>();
        Random rnd = new Random();
        Map<PictureFormat, Collection<File>> filesByPictureFormat = getFilesByPictureFormat(formats);
        for (User user : users){
            int quantity = rnd.nextInt(maxQuantity) + 1;
            Action[] actions = Action.values();
            for (int i = 0; i < quantity; i++){
                Campaign campaign = new Campaign(
                        null,
                        "Campaign_" + i,
                        "Title of Campaign_" + i,
                        "http://somesite_" + i + ".com",
                        BigDecimal.valueOf(100 + rnd.nextInt(50)),
                        BigDecimal.valueOf(1000 + rnd.nextInt(1000)),
                        actions[rnd.nextInt(actions.length)],
                        Status.WORKING
                );

                Map<PictureFormat, File> files = getFiles(filesByPictureFormat, formats);
                createCampaign(user, files, campaign);
                campaigns.add(campaign);
            }
        }
        return campaigns;
    }

    private static Campaign createCampaign(
            User userCustomer,
            Map<PictureFormat, File> files,
            Campaign campaign
    ) throws DbException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            Customer customer = DaoFactory.getCustomerDao().getByUserId(userCustomer.getId());
            campaign.setCustomer(customer);
            DaoFactory.getCampaignDao().create(campaign);

            Set<Picture> pictures = getPictures(customer.getId(), campaign.getId(), files);
            campaign.setPictures(pictures);

            transaction.commit();
            return campaign;
        } catch (HibernateException | NoResultException | NullPointerException | IOException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }

    private static Set<Picture> getPictures(long customerId, long campaignId, Map<PictureFormat, File> files)
            throws IOException {
        Set<Picture> pictures = new HashSet<>();
        String pathForUploadFiles = Links.createFoldersIfNotExist(customerId, campaignId, PATH_TO_UPLOAD_FOLDER);
        for (Map.Entry<PictureFormat, File> entry : files.entrySet()){
            PictureFormat format = DaoFactory.getPictureFormatDao().get(entry.getKey()).get(0);
            String filename = customerId + "_" + campaignId + "_" + format.getId() + ".png";
            File newFile = new File(pathForUploadFiles + "/" + filename);
            FileUtils.copyFile(entry.getValue(), newFile);
            pictures.add(new Picture(filename, format));
        }
        return pictures;
    }

    private static Map<PictureFormat, File> getFiles(
            Map<PictureFormat, Collection<File>> filesByPictureFormat,
            List<PictureFormat> formats)
    {
        Map<PictureFormat, File> files = new HashMap<>();
        int size = filesByPictureFormat.get(formats.get(0)).size();
        Random rnd = new Random();
        int numFile = rnd.nextInt(size);
        for (Map.Entry<PictureFormat, Collection<File>> entry : filesByPictureFormat.entrySet()){
            List<File> values = new ArrayList<>(entry.getValue());
            files.put(entry.getKey(), values.get(numFile));
        }
        return files;
    }

    private static Map<PictureFormat, Collection<File>> getFilesByPictureFormat(List<PictureFormat> formats){
        Map<PictureFormat, Collection<File>> pictures = new HashMap<>();
        pictures.put(formats.get(0), FileUtils.listFiles(
                new File("test_pictures/611_120"),
                new String[] {"png"},
                false)
        );
        pictures.put(formats.get(1), FileUtils.listFiles(
                new File("test_pictures/813_120"),
                new String[] {"png"},
                false)
        );
        return pictures;
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

    private static List<Viewer> createViewers(int quantity){
        List<Viewer> viewers = new ArrayList<>();
        Random rnd = new Random();
        for (int i = 0; i < quantity; i++){
//            Viewer viewer = new Viewer("Name_" + rnd.nextInt(100000), createIp());
            Viewer viewer = new Viewer(generateName(), createIp());
            viewers.add(viewer);
        }
        return viewers;
    }

    private static String generateName()
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
            try{
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
            } catch (NotFoundException e){
                System.out.println("NotFoundException");
            }
        }
        return requests;
    }

    private static RequestResource.UpdateRequestDto getUpdateRequestDto(RequestResource requestResource){
        RequestResource.UpdateRequestDto updateRequestDto = requestResource.new UpdateRequestDto();
        Random rnd = new Random();
        updateRequestDto.setClickOn(rnd.nextBoolean());
        updateRequestDto.setActualShowTime(rnd.nextInt(100));
        return updateRequestDto;
    }

}
