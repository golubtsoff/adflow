package rest.customer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entity.users.Action;
import entity.users.PictureFormat;
import entity.users.Status;
import entity.users.customer.Campaign;
import entity.users.customer.Picture;
import entity.users.user.Role;
import entity.users.user.UserToken;
import exception.BadRequestException;
import exception.ConflictException;
import exception.NotFoundException;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import rest.Roles;
import rest.admin.strategy.CampaignExclusionStrategy;
import rest.statistics.dto.DetailStatisticsDto;
import rest.statistics.dto.ShortStatisticsDto;
import rest.users.autentication.Secured;
import service.CampaignService;
import service.StatisticsService;
import util.JsonHelper;
import util.Links;

import javax.imageio.ImageIO;
import javax.persistence.OptimisticLockException;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;

@Path("/customer/campaigns")
@Secured
@Roles(Role.CUSTOMER)
public class CampaignResource {

    @Context
    HttpHeaders headers;

    public class CampaignDto{
        private String title;
        private String description;
        private String pathOnClick;
        private Set<Picture> pictures;
        private BigDecimal dailyBudget;
        private BigDecimal cpmRate;
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

        public String getPathOnClick() {
            return pathOnClick;
        }

        public void setPathOnClick(String pathOnClick) {
            this.pathOnClick = pathOnClick;
        }

        public Set<Picture> getPictures() {
            return pictures;
        }

        public void setPictures(Set<Picture> pictures) {
            this.pictures = pictures;
        }

        public BigDecimal getDailyBudget() {
            return dailyBudget;
        }

        public void setDailyBudget(BigDecimal dailyBudget) {
            this.dailyBudget = dailyBudget;
        }

        public BigDecimal getCpmRate() {
            return cpmRate;
        }

        public void setCpmRate(BigDecimal cpmRate) {
            this.cpmRate = cpmRate;
        }

        public Action getAction() {
            return action;
        }

        public void setAction(Action action) {
            this.action = action;
        }
    }

    // TODO: copy picture's images in directory on server's side from client
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response create(@FormDataParam("campaign") String content,
                           @FormDataParam("files") List<FormDataBodyPart> parts){
        try{
            Gson gson = JsonHelper.getGson();
            CampaignDto campaignDto = gson.fromJson(content, CampaignDto.class);
            if (campaignDto == null)
                return Response.status(Response.Status.BAD_REQUEST).build();

            long userId = Long.parseLong(headers.getHeaderString(UserToken.UID));
            Campaign campaign = CampaignService.create(userId, campaignDto, getImages(parts));
            if (campaign.getStatus() == Status.REMOVED)
                return Response.status(Response.Status.NOT_FOUND).build();

            setFullPictureLinks(campaign);
            Gson dOut = new GsonBuilder()
                    .setPrettyPrinting()
                    .setExclusionStrategies(new rest.customer.strategy.CampaignExclusionStrategy())
                    .create();

            return Response.ok(dOut.toJson(campaign)).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (ConflictException e) {
            return Response.status(Response.Status.CONFLICT).build();
        } catch (IOException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    private void setFullPictureLinks(Campaign campaign) {
        if (campaign.getPictures() == null) return;
        for (Picture picture : campaign.getPictures()){
            String linkToImage = Links.getHost()
                    + "/" + campaign.getCustomer().getId()
                    + "/" + campaign.getId()
                    + "/" + picture.getFileName();
            picture.setFileName(linkToImage);
        }
    }

    private Map<PictureFormat, BufferedImage> getImages(List<FormDataBodyPart> parts)
            throws IOException, ConflictException {
        Map<PictureFormat, BufferedImage> images = new HashMap<>();
        BufferedImage image;
        for (FormDataBodyPart part : parts){
            try (InputStream is = part.getValueAs(InputStream.class)){
                image = ImageIO.read(is);
                PictureFormat format = new PictureFormat(image.getWidth(), image.getHeight());
                images.put(format, image);
            }
        }
        if (parts.size() != images.size()) throw new ConflictException();
        return images;
    }

    // TODO: предусмотреть ограничение по максимальной длине списка. Например, 100 пользователей.
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response readAll(){
        try{
            long userId = Long.parseLong(headers.getHeaderString(UserToken.UID));
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
            long userId = Long.parseLong(headers.getHeaderString(UserToken.UID));
            Campaign campaign = CampaignService.getWithChecking(userId, campaignId);
            if (campaign.getStatus() == Status.REMOVED){
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            setFullPictureLinks(campaign);
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
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
            @PathParam("cid") long campaignId,
            @FormDataParam("campaign") String content,
            @FormDataParam("files") List<FormDataBodyPart> parts
    ){
        try{
            Gson gson = JsonHelper.getGson();
            CampaignDto campaignDto = gson.fromJson(content, CampaignDto.class);
            if (campaignDto == null)
                return Response.status(Response.Status.BAD_REQUEST).build();

            long userId = Long.valueOf(headers.getHeaderString(UserToken.UID));
            Campaign campaignFromBase = CampaignService.updateByCustomer(
                    userId, campaignId, campaignDto, getImages(parts));

            setFullPictureLinks(campaignFromBase);
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

    @GET
    @Path("statistics")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatistics(@QueryParam("from") String from, @QueryParam("to") String to){
        try{
            long userId = Long.parseLong(headers.getHeaderString(UserToken.UID));
            ShortStatisticsDto shortStatisticsDto
                    = StatisticsService.getShortCampaignStatistics(userId, from, to);
            return Response.ok(JsonHelper.getGson().toJson(shortStatisticsDto)).build();
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("{cid}/statistics")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatistics(
            @PathParam("cid") long campaignId,
            @QueryParam("from") String from,
            @QueryParam("to") String to,
            @QueryParam("group") String group){

        try{
            long userId = Long.parseLong(headers.getHeaderString(UserToken.UID));
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
