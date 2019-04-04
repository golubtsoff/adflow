package rest.autentication;

import entity.users.user.UserToken;
import exception.DbException;
import service.UserService;

import javax.ws.rs.Path;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("/authenticate")
public class AuthenticationEndpoint {

    @Context
    UriInfo uriInfo;

    @POST
    @Path("/signin")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response authenticateUser(@FormParam("login") String login,
                                     @FormParam("password") String password) {
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
    @Path("/signout")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response revokeAuthentication(@Context HttpHeaders headers) {
        long userId = Long.valueOf(headers.getHeaderString("uid"));
        try {
            UserService.signOut(userId);
        } catch (DbException e) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        return Response.ok().build();
    }
}
