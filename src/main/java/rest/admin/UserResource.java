package rest.admin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import entity.users.Status;
import entity.users.user.*;
import exception.DbException;
import rest.Roles;
import rest.users.autentication.Secured;
import service.UserService;
import util.JsonHelper;

import javax.persistence.OptimisticLockException;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;

@Path("/admin/users")
@Secured
@Roles(Role.ADMIN)
public class UserResource {

    public static final String URI_FIELD_NAME = "uri";

    @Context
    HttpHeaders headers;

    @Context
    private UriInfo uriInfo;

    private class Credential{
        private String login;
        private String password;
    }

    // TODO: предусмотреть ограничение по максимальной длине списка. Например, 100 пользователей.
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response readAll(){
        try{
            Gson dOut = new GsonBuilder()
                    .setPrettyPrinting()
                    .setExclusionStrategies(new UserExclusionStrategy())
                    .create();
            return Response.ok(dOut.toJson(UserService.getAll())).build();
        } catch (DbException e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("{uid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response read(@PathParam("uid") long id){
        try {
            User user = UserService.get(id);
            if (user == null)
                return Response.status(Response.Status.NOT_FOUND).build();
            return Response.ok(getJsonString(user)).build();
        } catch (DbException e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String getJsonString(User user){
        JsonObject jo = JsonHelper.getGson().toJsonTree(user).getAsJsonObject();
        jo.remove("hash");
        return JsonHelper.getGson().toJson(jo);
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAdmin(String content){
        try {
            Gson gson = JsonHelper.getGson();
            Credential credential = gson.fromJson(content, Credential.class);

            User user = UserService.signUp(credential.login.toLowerCase(), credential.password, Role.ADMIN);
            if (user == null)
                return Response.status(Response.Status.FORBIDDEN).build();
            URI userUri = uriInfo.getAbsolutePathBuilder().path(user.getId().toString()).build();

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(URI_FIELD_NAME, userUri.toString());

            return Response.ok(gson.toJson(jsonObject)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @PUT
    @Path("{uid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("uid") long id, String content){
        try{
            Gson gson = JsonHelper.getGson();
            User userFromClient = gson.fromJson(content, User.class);
            if (userFromClient == null)
                return Response.notModified().build();

            User userFromBase = UserService.get(id);
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
        Status status = userFromClient.getStatus();
        if (contact != null)
            userFromBase.setContact(contact);
        if (person != null)
            userFromBase.setPerson(person);
        if (status != null)
            userFromBase.setStatus(status);
        return userFromBase;
    }

    @DELETE
    @Path("{uid}")
    public Response delete(@PathParam("uid") long id){
        try{
            UserService.delete(id);
            return Response.noContent().build();
        } catch (DbException e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e){
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception e){
            return Response.notModified().build();
        }
    }


}
