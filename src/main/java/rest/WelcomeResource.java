package rest;

import util.JsonHelper;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class WelcomeResource {
    private static String GREETINGS = "Welcome to AdFlow!";

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response welcome(){
        try{
            return Response.ok(JsonHelper.getGson().toJson(GREETINGS)).build();
        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
