package rest;

import service.UserService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/signup")
public class Registration {

    public static final String LOGIN_FORM_NAME = "login";
    public static final String PASSWORD_FORM_NAME = "password";
    public static final String ROLE_FORM_NAME = "role";

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response registerUser(@FormParam(LOGIN_FORM_NAME) String login,
                                 @FormParam(PASSWORD_FORM_NAME) String password,
                                 @FormParam(ROLE_FORM_NAME)String roleString)
    {
        try {
            if (UserService.signUpExceptAdministrator(login.toLowerCase(), password, roleString) == null)
                return Response.status(Response.Status.FORBIDDEN).build();
            return Response.ok().build();
        } catch (Exception e) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }
}
