package rest.admin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import entity.users.customer.Campaign;
import entity.users.customer.Customer;
import entity.users.user.Role;
import entity.users.user.User;
import exception.DbException;
import rest.Roles;
import rest.admin.strategy.CampaignExclusionStrategy;
import rest.users.autentication.Secured;
import service.CampaignService;
import service.UserService;
import util.JsonHelper;

import javax.persistence.OptimisticLockException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

@Path("/admin/users")
@Secured
@Roles(Role.ADMIN)
public class CampaignResource {

    // TODO: предусмотреть ограничение по максимальной длине списка. Например, 100 пользователей.
    @GET
    @Path("{uid}/campaigns")
    @Produces(MediaType.APPLICATION_JSON)
    public Response readAll(@PathParam("uid") long userId){
        try{
            List<Campaign> campaigns = CampaignService.getAllByUserId(userId);
            if (campaigns == null)
                return Response.status(Response.Status.NOT_FOUND).build();

            Gson dOut = new GsonBuilder()
                    .setPrettyPrinting()
                    .setExclusionStrategies(new CampaignExclusionStrategy())
                    .create();
            return Response.ok(dOut.toJson(campaigns)).build();
        } catch (DbException e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("{uid}/campaigns/{cid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response read(@PathParam("uid") long userId, @PathParam("cid") long campaignId){
        try{
            Campaign campaign = CampaignService.getWithChecking(userId, campaignId);
            if (campaign == null)
                return Response.status(Response.Status.NOT_FOUND).build();
            return Response.ok(JsonHelper.getJsonStringExcludeFields(
                    campaign,
                    Arrays.asList("customer")
            )).build();
        } catch (DbException | ClassCastException e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PUT
    @Path("{uid}/campaigns/{cid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
            @PathParam("uid") long userId,
            @PathParam("cid") long campaignId,
            String content
    ){
        try{
            Gson gson = JsonHelper.getGson();
            Campaign campaign = gson.fromJson(content, Campaign.class);
            CampaignService.update(campaign);
            return Response.ok(gson.toJson(campaign)).build();
        } catch (DbException e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (OptimisticLockException e){
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception e){
            return Response.notModified().build();
        }
    }

}
