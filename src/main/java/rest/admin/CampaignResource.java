package rest.admin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entity.users.Status;
import entity.users.customer.Campaign;
import entity.users.user.Role;
import exception.BadRequestException;
import exception.NotFoundException;
import rest.Roles;
import rest.FieldsExclusionStrategy;
import rest.statistics.dto.DetailStatisticsDto;
import rest.statistics.dto.ShortStatisticsDto;
import rest.users.authentication.Secured;
import service.CampaignService;
import service.StatisticsService;
import util.JsonHelper;

import javax.persistence.OptimisticLockException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;

@Path("/admin/users")
@Secured
@Roles(Role.ADMIN)
public class CampaignResource {

    public class CampaignDto{
        private BigDecimal cpmRate;
        private Status status;

        public BigDecimal getCpmRate() {
            return cpmRate;
        }

        public void setCpmRate(BigDecimal cpmRate) {
            this.cpmRate = cpmRate;
        }

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }
    }

    @GET
    @Path("{uid}/campaigns")
    @Produces(MediaType.APPLICATION_JSON)
    public Response readAll(@PathParam("uid") long userId){
        try{
            List<Campaign> campaigns = CampaignService.getAllByUserId(userId);
            Gson dOut = new GsonBuilder()
                    .setPrettyPrinting()
                    .setExclusionStrategies(new FieldsExclusionStrategy(
                            "customer", "pictures", "removedDate"))
                    .create();
            return Response.ok(dOut.toJson(campaigns)).build();
        } catch (NotFoundException e){
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("{uid}/campaigns/{cid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response read(@PathParam("uid") long userId, @PathParam("cid") long campaignId){
        try{
            Campaign campaign = CampaignService.get(userId, campaignId);
            Gson dOut = new GsonBuilder()
                    .setPrettyPrinting()
                    .setExclusionStrategies(new FieldsExclusionStrategy("customer"))
                    .create();
            return Response.ok(dOut.toJson(campaign)).build();
        } catch (NotFoundException e){
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception e){
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
            CampaignDto campaignDto = gson.fromJson(content, CampaignDto.class);
            if (campaignDto == null)
                return Response.status(Response.Status.BAD_REQUEST).build();
            Campaign campaignFromBase = CampaignService.updateByAdmin(userId, campaignId, campaignDto);

            Gson dOut = new GsonBuilder()
                    .setPrettyPrinting()
                    .setExclusionStrategies(new FieldsExclusionStrategy("customer"))
                    .create();
            return Response.ok(dOut.toJson(campaignFromBase)).build();
        } catch (OptimisticLockException | NotFoundException e){
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DELETE
    @Path("{uid}/campaigns/{cid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(
            @PathParam("uid") long userId,
            @PathParam("cid") long campaignId
    ){
        try{
            CampaignService.delete(userId, campaignId);
            return Response.noContent().build();
        } catch (IllegalArgumentException | OptimisticLockException | NotFoundException e){
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("{uid}/campaigns/statistics")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatistics(
            @PathParam("uid") long userId,
            @QueryParam("from") String from,
            @QueryParam("to") String to){
        try{
            ShortStatisticsDto shortStatisticsDto
                    = StatisticsService.getShortCampaignStatistics(userId, from, to);
            return Response.ok(JsonHelper.getGson().toJson(shortStatisticsDto)).build();
        } catch (exception.BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("{uid}/campaigns/{cid}/statistics")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatistics(
            @PathParam("uid") long userId,
            @PathParam("cid") long campaignId,
            @QueryParam("from") String from,
            @QueryParam("to") String to,
            @QueryParam("group") String group){

        try{
            DetailStatisticsDto detailStatisticsDto
                    = StatisticsService.getDetailCampaignStatistics(userId, campaignId, from, to, group);
            return Response.ok(JsonHelper.getGson().toJson(detailStatisticsDto)).build();
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

}
