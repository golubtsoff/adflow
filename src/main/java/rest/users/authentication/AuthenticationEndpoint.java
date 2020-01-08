package rest.users.authentication;

import entity.users.user.UserToken;
import exception.DbException;
import service.UserService;

import javax.ws.rs.Path;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("/users/authentication")
public class AuthenticationEndpoint {

    public static final String LOGIN_FORM_NAME = "login";
    public static final String PASSWORD_FORM_NAME = "password";

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response authenticateUser(@FormParam(LOGIN_FORM_NAME) String login,
                                     @FormParam(PASSWORD_FORM_NAME) String password) {
        try {
            UserToken token = UserService.signIn(login.toLowerCase(), password);
            if (token == null)
                return Response.status(Response.Status.FORBIDDEN).build();
            return Response.ok(token.getToken()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @Secured
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response revokeAuthentication(@Context HttpHeaders headers) {
        try {
            long userId = Long.valueOf(headers.getHeaderString(UserToken.UID));
            UserService.signOut(userId);
        } catch (DbException e) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        return Response.ok().build();
    }
}
