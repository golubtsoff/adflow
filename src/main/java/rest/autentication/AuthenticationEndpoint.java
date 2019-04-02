package rest.autentication;

import entity.users.user.UserToken;
import service.UserService;

import javax.ws.rs.Path;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/signin")
public class AuthenticationEndpoint {
    @POST
    @Path("/")
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
}
