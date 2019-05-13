package rest.personal;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.users.Status;
import entity.users.user.User;
import entity.users.user.UserToken;
import exception.DbException;
import rest.users.autentication.Secured;
import service.UserService;
import util.JsonHelper;

import javax.persistence.OptimisticLockException;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

// TODO: сделать сброс и смену пароля пользователя
@Path("/profile")
public class ProfileResourse {

    @Context
    HttpHeaders headers;

    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(){
        try {
            long userId = Long.valueOf(headers.getHeaderString(UserToken.UID));
            User user = UserService.get(userId);
            return Response.ok(getJsonString(user)).build();
        } catch (Exception e){
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @PUT
    @Secured
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(String content){
        try{
            long userId = Long.valueOf(headers.getHeaderString(UserToken.UID));
            Gson gson = JsonHelper.getGson();
            User userFromClient = gson.fromJson(content, User.class);

            User userFromBase = UserService.get(userId);
            userFromBase.setContact(userFromClient.getContact());
            userFromBase.setPerson(userFromClient.getPerson());
            UserService.update(userFromBase);

            return Response.ok(getJsonString(userFromBase)).build();
        } catch (DbException e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (OptimisticLockException e){
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception e){
            return Response.notModified().build();
        }
    }

    @DELETE
    @Secured
    public Response deleteUser(){
        try{
            long userId = Long.valueOf(headers.getHeaderString(UserToken.UID));
            User user = UserService.get(userId);
            user.setStatus(Status.REMOVED);
            UserService.update(user);
            return Response.noContent().build();
        } catch (DbException e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e){
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception e){
            return Response.notModified().build();
        }
    }

    private String getJsonString(User user){
        JsonObject jo = JsonHelper.getGson().toJsonTree(user).getAsJsonObject();
        jo.remove("hash");
        jo.remove("status");
        return JsonHelper.getGson().toJson(jo);
    }
}
