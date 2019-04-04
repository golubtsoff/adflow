package rest.autentication;

import entity.users.user.UserToken;
import exception.DbException;
import service.UserService;

import javax.ws.rs.Path;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("/authenticate")
public class AuthenticationEndpoint {

    public static final String LOGIN_FORM_NAME = "login";
    public static final String PASSWORD_FORM_NAME = "password";
    public static final String AUTHENTICATE_USER_PATH = "/signin";
    public static final String REVOKE_AUTHENTICATION_PATH = "/signout";

    @Context
    UriInfo uriInfo;

    @POST
    @Path(AUTHENTICATE_USER_PATH)
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
    @POST
    @Path(REVOKE_AUTHENTICATION_PATH)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response revokeAuthentication(@Context HttpHeaders headers) {
        long userId = Long.valueOf(headers.getHeaderString(UserToken.UID));
        try {
            UserService.signOut(userId);
        } catch (DbException e) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        return Response.ok().build();
    }
}
