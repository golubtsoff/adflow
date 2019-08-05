package rest.admin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.DaoFactory;
import dao.DbAssistant;
import entity.users.PictureFormat;
import entity.users.user.Role;
import exception.ConflictException;
import exception.DbException;
import exception.NotFoundException;
import exception.ServiceException;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import rest.Roles;
import rest.users.autentication.Secured;
import service.PictureFormatService;
import util.JsonHelper;
import util.NullAware;

import javax.persistence.NoResultException;
import javax.persistence.OptimisticLockException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Path("/admin/pictureformat")
@Secured
@Roles(Role.ADMIN)
public class PictureFormatResource {

    public class PictureFormatDto{
        private int width;
        private int height;

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(String content){
        try{
            Gson gson = JsonHelper.getGson();
            PictureFormatDto pictureFormatDto = gson.fromJson(content, PictureFormatDto.class);
            if ((pictureFormatDto == null)
                    || (pictureFormatDto.getWidth() <=0)
                    || (pictureFormatDto.getHeight() <= 0))
                return Response.status(Response.Status.BAD_REQUEST).build();

            PictureFormat pictureFormat = PictureFormatService.create(pictureFormatDto);
            return Response.ok(gson.toJson(pictureFormat)).build();
        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response readAll(){
        try{
            List<PictureFormat> formats = PictureFormatService.getAll();
            return Response.ok(JsonHelper.getGson().toJson(formats)).build();
        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("{pfid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response read(@PathParam("pfid") long pictureFormatId){
        try{
            PictureFormat pictureFormat = PictureFormatService.get(pictureFormatId);
            if (pictureFormat == null){
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(JsonHelper.getGson().toJson(pictureFormat)).build();
        } catch (NotFoundException e){
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DELETE
    @Path("{pfid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response safeDelete(@PathParam("pfid") long pictureFormatId){
        try{
            PictureFormatService.delete(pictureFormatId);
            return Response.noContent().build();
        } catch (IllegalArgumentException | OptimisticLockException | NotFoundException e){
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (ConflictException e){
            return Response.noContent().build();
        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
