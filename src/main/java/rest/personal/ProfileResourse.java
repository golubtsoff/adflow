package rest.personal;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import entity.users.Status;
import entity.users.user.Contact;
import entity.users.user.Person;
import entity.users.user.User;
import entity.users.user.UserToken;
import exception.DbException;
import rest.users.autentication.Secured;
import service.UserService;
import util.Hash;
import util.JsonHelper;

import javax.persistence.OptimisticLockException;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

// TODO: сделать сброс и смену пароля пользователя
@Path("/profile")
@Secured
public class ProfileResourse {

    @Context
    HttpHeaders headers;

    @GET
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
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(String content){
        try{
            long userId = Long.valueOf(headers.getHeaderString(UserToken.UID));
            Gson gson = JsonHelper.getGson();
            User userFromClient = gson.fromJson(content, User.class);
            if (userFromClient == null)
                return Response.notModified().build();

            User userFromBase = UserService.get(userId);
            if (userFromBase == null)
                return Response.status(Response.Status.NOT_FOUND).build();

            userFromBase = partlyUpdateUser(userFromClient, userFromBase);
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

    private User partlyUpdateUser(User userFromClient, User userFromBase){
        Contact contact = userFromClient.getContact();
        Person person = userFromClient.getPerson();
        if (contact != null)
            userFromBase.setContact(contact);
        if (person != null)
            userFromBase.setPerson(person);
        return userFromBase;
    }

    @DELETE
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

    @PUT
    @Path("/password")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePassword(String content){
        try{
            JsonObject jsonObject = new JsonParser().parse(content).getAsJsonObject();
            String password = jsonObject.get("password").getAsString();
            String hash = Hash.getHash(password);

            long userId = Long.valueOf(headers.getHeaderString(UserToken.UID));
            User user = UserService.get(userId);
            user.setHash(hash);
            UserService.update(user);

            return Response.ok().build();
        } catch (DbException e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

}
