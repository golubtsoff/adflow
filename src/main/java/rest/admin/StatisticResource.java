package rest.admin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entity.users.customer.Campaign;
import entity.users.user.Role;
import exception.NotFoundException;
import rest.Roles;
import rest.admin.strategy.CampaignExclusionStrategy;
import rest.users.autentication.Secured;
import service.CampaignService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

//@Path("/admin/users")
//@Secured
//@Roles(Role.ADMIN)
public class StatisticResource {

//    @GET
//    @Path("{uid}/campaigns/statistics")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response readAll(@PathParam("uid") long userId){
//        try{
//            List<Campaign> campaigns = CampaignService.getAllByUserId(userId);
//            Gson dOut = new GsonBuilder()
//                    .setPrettyPrinting()
//                    .setExclusionStrategies(new CampaignExclusionStrategy())
//                    .create();
//            return Response.ok(dOut.toJson(campaigns)).build();
//        } catch (NotFoundException e){
//            return Response.status(Response.Status.NOT_FOUND).build();
//        } catch (Exception e){
//            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
//        }
//    }
}
