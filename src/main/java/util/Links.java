package util;

import entity.statistics.Options;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class Links {
    private static final String PATH = "/adv_options.properties";
    private static final String HOST_TO_UPLOADS_IMAGES_NAME = "host_to_uploads_images";
    private static final String HOST_TO_UPLOADS_IMAGES_DEFAULT_VALUE = "http://localhost:8080/";
    private static final String PATH_TO_UPLOADS_IMAGES_NAME = "file_upload_path";
    private static final String PATH_TO_UPLOADS_IMAGES_DEFAULT_VALUE = "../webapps/uploads";

    private static String host;
    private static String pathToUploadsImages;

    static{
        try (InputStream is = Options.class.getResourceAsStream(PATH)) {
            Properties props = new Properties();
            props.load(is);
            host = props.getProperty(
                    HOST_TO_UPLOADS_IMAGES_NAME,
                    HOST_TO_UPLOADS_IMAGES_DEFAULT_VALUE
            );
            pathToUploadsImages = Paths.get(props.getProperty(
                    PATH_TO_UPLOADS_IMAGES_NAME,
                    PATH_TO_UPLOADS_IMAGES_DEFAULT_VALUE
            )).toAbsolutePath().normalize().toString();
            if (!Files.exists(Paths.get(pathToUploadsImages))){
                Files.createDirectory(Paths.get(pathToUploadsImages));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getHost() {
        return host;
    }

    public static String getPathToUploadsImages() {
        return pathToUploadsImages;
    }

    public static String getUrlToImage(long customerId, long campaignId, String filename){
        return Links.getHost()
                + "/" + customerId
                + "/" + campaignId
                + "/" + filename;
    }

    public static String createFoldersIfNotExist(long customerId, long campaignId) throws IOException {
        return createFoldersIfNotExist(customerId, campaignId, pathToUploadsImages);
    }

    public static String createFoldersIfNotExist(long customerId, long campaignId, String path) throws IOException {
        long[] idArray = {customerId, campaignId};
        for (long id : idArray){
            path = Paths.get(path, String.valueOf(id)).toString();
            if (Files.notExists(Paths.get(path))){
                Files.createDirectory(Paths.get(path));
            }
        }
        return path;
    }

    public static void deleteFolder(long customerId, long campaignId) throws IOException {
        Path path = Paths.get(
                Links.getPathToUploadsImages(),
                String.valueOf(customerId),
                String.valueOf(campaignId))
            .normalize();
        File dir = new File(path.toString());
        FileUtils.forceDelete(dir);
    }

    public static void deleteFolder(Long customerId) throws IOException {
        Path path = Paths.get(
                Links.getPathToUploadsImages(),
                String.valueOf(customerId))
                .normalize();
        File dir = new File(path.toString());
        FileUtils.forceDelete(dir);
    }
}
