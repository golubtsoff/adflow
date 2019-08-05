package rest.partner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entity.users.Action;
import entity.users.Status;
import entity.users.customer.Campaign;
import entity.users.PictureFormat;
import entity.users.partner.Platform;
import entity.users.user.Role;
import entity.users.user.UserToken;
import exception.ConflictException;
import exception.NotFoundException;
import rest.Roles;
import rest.admin.strategy.CampaignExclusionStrategy;
import rest.users.autentication.Secured;
import service.CampaignService;
import service.PlatformService;
import util.JsonHelper;

import javax.persistence.OptimisticLockException;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Path("/partner/platforms")
@Secured
@Roles(Role.PARTNER)
public class PlatformResource {

    @Context
    HttpHeaders headers;

    public class PlatformDto {
        private String title;
        private String description;
        private BigDecimal cpmRate;
        private PictureFormat pictureFormat;
        private Action action;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public BigDecimal getCpmRate() {
            return cpmRate;
        }

        public void setCpmRate(BigDecimal cpmRate) {
            this.cpmRate = cpmRate;
        }

        public PictureFormat getPictureFormat() {
            return pictureFormat;
        }

        public void setPictureFormat(PictureFormat pictureFormat) {
            this.pictureFormat = pictureFormat;
        }

        public Action getAction() {
            return action;
        }

        public void setAction(Action action) {
            this.action = action;
        }
    }

//    @POST
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    public Response create(String content){
//        try{
//            Gson gson = JsonHelper.getGson();
//            PlatformDto platformDto = gson.fromJson(content, PlatformDto.class);
//            if (platformDto == null)
//                return Response.status(Response.Status.BAD_REQUEST).build();
//
//            long userId = Long.valueOf(headers.getHeaderString(UserToken.UID));
//            Platform platform = PlatformService.create(userId, platformDto);
//            if (platform == null || platform.getStatus() == Status.REMOVED)
//                return Response.status(Response.Status.NOT_FOUND).build();
//
//            Gson dOut = new GsonBuilder()
//                    .setPrettyPrinting()
//                    .setExclusionStrategies(new rest.partner.strategy.PlatformExclusionStrategy())
//                    .create();
//
//            return Response.ok(dOut.toJson(platform)).build();
//        } catch (NotFoundException e) {
//            return Response.status(Response.Status.NOT_FOUND).build();
//        } catch (ConflictException e) {
//            return Response.status(Response.Status.CONFLICT).build();
//        } catch (Exception e){
//            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
//        }
//    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response readAll(){
        try{
            long userId = Long.valueOf(headers.getHeaderString(UserToken.UID));
            List<Campaign> campaigns = CampaignService.getAllByUserId(userId);

            List<Campaign> notRemovedCampaigns = new ArrayList<>();
            for (Campaign campaign : campaigns){
                if (campaign.getStatus() != Status.REMOVED)
                    notRemovedCampaigns.add(campaign);
            }

            Gson dOut = new GsonBuilder()
                    .setPrettyPrinting()
                    .setExclusionStrategies(new CampaignExclusionStrategy())
                    .create();
            return Response.ok(dOut.toJson(notRemovedCampaigns)).build();
        } catch (NotFoundException e){
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("{cid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response read(@PathParam("cid") long campaignId){
        try{
            long userId = Long.valueOf(headers.getHeaderString(UserToken.UID));
            Campaign campaign = CampaignService.getWithChecking(userId, campaignId);
            if (campaign.getStatus() == Status.REMOVED){
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            Gson dOut = new GsonBuilder()
                    .setPrettyPrinting()
                    .setExclusionStrategies(new rest.customer.strategy.CampaignExclusionStrategy())
                    .create();

            return Response.ok(dOut.toJson(campaign)).build();
        } catch (NotFoundException e){
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PUT
    @Path("{cid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
            @PathParam("cid") long campaignId,
            String content
    ){
        try{
            Gson gson = JsonHelper.getGson();
            PlatformDto campaignDto = gson.fromJson(content, PlatformDto.class);
            if (campaignDto == null)
                return Response.status(Response.Status.BAD_REQUEST).build();

            long userId = Long.valueOf(headers.getHeaderString(UserToken.UID));
            Campaign campaignFromBase = CampaignService.updateExcludeNullWithChecking(userId, campaignId, campaignDto);

            Gson dOut = new GsonBuilder()
                    .setPrettyPrinting()
                    .setExclusionStrategies(new rest.customer.strategy.CampaignExclusionStrategy())
                    .create();

            return Response.ok(dOut.toJson(campaignFromBase)).build();
        } catch (OptimisticLockException | NotFoundException e){
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (ConflictException e) {
            return Response.status(Response.Status.CONFLICT).build();
        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DELETE
    @Path("{cid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(
            @PathParam("cid") long campaignId
    ){
        try{
            long userId = Long.valueOf(headers.getHeaderString(UserToken.UID));
            CampaignService.setStatusRemovedWithChecking(userId, campaignId);
            return Response.noContent().build();
        } catch (IllegalArgumentException | OptimisticLockException | NotFoundException e){
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

}
