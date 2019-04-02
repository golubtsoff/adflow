package rest;

import entity.users.user.Role;
import entity.users.user.User;
import service.UserService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/signup")
public class Registration {
    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response registerUser(@FormParam("login") String login,
                                 @FormParam("password") String password,
                                 @FormParam("role")String roleString)
    {
        try {
            if (UserService.signUp(login.toLowerCase(), password, roleString) == null)
                return Response.status(Response.Status.FORBIDDEN).build();
            return Response.ok().build();
        } catch (Exception e) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }
}
