package rest;

import com.google.gson.Gson;
import entity.users.user.Role;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import rest.customer.CampaignResource;
import rest.users.authentication.Secured;
import util.JsonHelper;

import javax.imageio.ImageIO;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

//TODO: remove class
@Path("/")
@Secured
@Roles(Role.CUSTOMER)
public class TestRest {

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/upload")
    public Response saveFile(
        @FormDataParam("file") InputStream is,
        @FormDataParam("file") FormDataContentDisposition fileDisposition
    ) throws IOException {
        OutputStream os = Files.newOutputStream(Paths.get("test.png"));
        try{
            os.write(IOUtils.toByteArray(is));
            os.flush();
            return Response.ok().build();
        } catch (Exception e){
            e.printStackTrace();
            return Response.serverError().build();
        } finally {
            is.close();
            os.close();
        }
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/uploadAll")
    public Response saveAllFiles(
            @FormDataParam("file1") InputStream is1,
            @FormDataParam("file1") FormDataContentDisposition fileDisposition1,
            @FormDataParam("file2") InputStream is2,
            @FormDataParam("file2") FormDataContentDisposition fileDisposition2
    ) throws IOException {
        OutputStream os1 = Files.newOutputStream(Paths.get("test1.png"));
        OutputStream os2 = Files.newOutputStream(Paths.get("test2.png"));
        try{
            os1.write(IOUtils.toByteArray(is1));
            os1.flush();
            os2.write(IOUtils.toByteArray(is2));
            os2.flush();
            return Response.ok().build();
        } catch (Exception e){
            e.printStackTrace();
            return Response.serverError().build();
        } finally {
            is1.close();
            os1.close();
            is2.close();
            os2.close();
        }
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/uploadList")
    public Response saveListFiles(
            @FormDataParam("campaign") String content,
            @FormDataParam("files") List<FormDataBodyPart> parts
    ) {

        Gson gson = JsonHelper.getGson();
        CampaignResource.CampaignDto campaignDto = gson.fromJson(content, CampaignResource.CampaignDto.class);

        int i = 0;
        String pathForUpload = Paths.get(".").toAbsolutePath().getParent().getParent()
                .toString() + "/webapps/uploads/";
        BufferedImage bufferedImage;
        for (FormDataBodyPart part : parts) {
            FormDataContentDisposition disp = part
                    .getFormDataContentDisposition();
            String filename = i++ + ".png";
            try (InputStream is = part.getValueAs(InputStream.class);
                OutputStream os = Files.newOutputStream(Paths.get(pathForUpload + filename))){

                bufferedImage = ImageIO.read(is);
                int fileWidth = bufferedImage.getWidth();
                int fileHeight = bufferedImage.getHeight();
                System.out.println(fileWidth + ":" + fileHeight);
                ImageIO.write(bufferedImage, "png", os);
                os.flush();
            } catch (Exception e){
                e.printStackTrace();
                return Response.serverError().build();
            }
        }
        return Response.ok().build();
    }


}
