package rest.users;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.users.user.User;
import exception.DbException;
import service.UserService;
import util.JsonHelper;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

@Path("/users")
public class UserResource {

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
            Gson gson = JsonHelper.getGson();
            Credential credential = gson.fromJson(content, Credential.class);

            User user = UserService.signUpExceptAdministrator(credential.login.toLowerCase(), credential.password, credential.role);
            if (user == null)
                return Response.status(Response.Status.FORBIDDEN).build();
            URI userUri = uriInfo.getAbsolutePathBuilder().path(user.getId().toString()).build();

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(URI_FIELD_NAME, userUri.toString());

            return Response.ok(gson.toJson(jsonObject)).build();
        } catch (DbException e){
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

}
