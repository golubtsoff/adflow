package rest.partner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entity.users.Action;
import entity.users.Status;
import entity.users.PictureFormat;
import entity.users.partner.Platform;
import entity.users.partner.PlatformToken;
import entity.users.user.Role;
import entity.users.user.UserToken;
import exception.ConflictException;
import exception.NotFoundException;
import rest.Roles;
import rest.users.autentication.Secured;
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

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(String content){
        try{
            Gson gson = JsonHelper.getGson();
            PlatformDto platformDto = gson.fromJson(content, PlatformDto.class);
            if (platformDto == null
                    || platformDto.getPictureFormat() == null)
                return Response.status(Response.Status.BAD_REQUEST).build();

            long userId = Long.valueOf(headers.getHeaderString(UserToken.UID));
            Platform platform = PlatformService.create(userId, platformDto);
            if (platform == null || platform.getStatus() == Status.REMOVED)
                return Response.status(Response.Status.NOT_FOUND).build();

            return Response.ok(getOutJson().toJson(platform)).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (ConflictException e) {
            return Response.status(Response.Status.CONFLICT).build();
        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    private Gson getOutJson(){
        return new GsonBuilder()
            .setPrettyPrinting()
            .setExclusionStrategies(new rest.partner.strategy.PlatformExclusionStrategy())
            .create();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response readAll(){
        try{
            long userId = Long.valueOf(headers.getHeaderString(UserToken.UID));
            List<Platform> platforms = PlatformService.getAllByUserId(userId);

            List<Platform> notRemovedPlatforms = new ArrayList<>();
            for (Platform platform : platforms){
                if (platform.getStatus() != Status.REMOVED)
                    notRemovedPlatforms.add(platform);
            }
            return Response.ok(getOutJson().toJson(notRemovedPlatforms)).build();
        } catch (NotFoundException e){
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("{pid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response read(@PathParam("pid") long platformId){
        try{
            long userId = Long.valueOf(headers.getHeaderString(UserToken.UID));
            Platform platform = PlatformService.getWithChecking(userId, platformId);
            if (platform.getStatus() == Status.REMOVED){
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            return Response.ok(getOutJson().toJson(platform)).build();
        } catch (NotFoundException e){
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PUT
    @Path("{pid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
            @PathParam("pid") long platformId,
            String content
    ){
        try{
            Gson gson = JsonHelper.getGson();
            PlatformDto platformDto = gson.fromJson(content, PlatformDto.class);
            if (platformDto == null)
                return Response.status(Response.Status.BAD_REQUEST).build();

            long userId = Long.valueOf(headers.getHeaderString(UserToken.UID));
            Platform platform = PlatformService.updateExcludeNullWithChecking(userId, platformId, platformDto);

            return Response.ok(getOutJson().toJson(platform)).build();
        } catch (OptimisticLockException | NotFoundException e){
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (ConflictException e) {
            return Response.status(Response.Status.CONFLICT).build();
        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DELETE
    @Path("{pid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(
            @PathParam("pid") long platformId
    ){
        try{
            long userId = Long.valueOf(headers.getHeaderString(UserToken.UID));
            PlatformService.setStatusRemovedWithChecking(userId, platformId);
            return Response.noContent().build();
        } catch (IllegalArgumentException | OptimisticLockException | NotFoundException e){
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("{pid}/token")
    @Produces(MediaType.APPLICATION_JSON)
    public Response readToken(@PathParam("pid") long platformId){
        try{
            long userId = Long.valueOf(headers.getHeaderString(UserToken.UID));
            Platform platform = PlatformService.getWithChecking(userId, platformId);
            if (platform.getStatus() == Status.REMOVED){
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            PlatformToken token = PlatformService.getOrCreateToken(platform);
            return Response.ok(token.getToken()).build();
        } catch (NotFoundException e){
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PUT
    @Path("{pid}/token")
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("pid") long platformId){
        try{
            long userId = Long.valueOf(headers.getHeaderString(UserToken.UID));
            Platform platform = PlatformService.getWithChecking(userId, platformId);
            if (platform.getStatus() == Status.REMOVED){
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            PlatformToken token = PlatformService.updateToken(platform);
            if (token == null){
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            return Response.ok(token.getToken()).build();
        } catch (OptimisticLockException | NotFoundException e){
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

}
