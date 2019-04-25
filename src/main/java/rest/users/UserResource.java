package rest.users;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import entity.users.user.User;
import exception.DbException;
import service.UserService;

import javax.persistence.OptimisticLockException;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

@Path("/users")
public class UserResource {

    public static final String LOGIN_FIELD_NAME = "login";
    public static final String PASSWORD_FIELD_NAME = "password";
    public static final String ROLE_FIELD_NAME = "role";

    public static final String URI_FIELD_NAME = "uri";

    @Context
    private UriInfo uriInfo;


    private class Credential{
        private String login;
        private String password;
        private String role;
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(String content){
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Credential credential = gson.fromJson(content, Credential.class);

            User user = UserService.signUpExceptAdministrator(credential.login.toLowerCase(), credential.password, credential.role);
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

    @GET
    @Path("{uid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response read(@PathParam("uid") long id){
        try {
            User user = UserService.get(id);
            if (user == null) return Response.status(Response.Status.NOT_FOUND).build();

            Gson dOut = new GsonBuilder().setPrettyPrinting().create();
            return Response.ok(dOut.toJson(user)).build();
        } catch (DbException e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response readAll(){
        try{
            Gson dOut = new GsonBuilder().setPrettyPrinting().create();
            return Response.ok(dOut.toJson(UserService.getAll())).build();
        } catch (DbException e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PUT
    @Path("{uid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("uid") long id, String content){
        try{
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            User user = gson.fromJson(content, User.class);
            UserService.update(user);
            return Response.ok(gson.toJson(user)).build();
        } catch (DbException e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (OptimisticLockException e){
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception e){
            return Response.notModified().build();
        }
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
