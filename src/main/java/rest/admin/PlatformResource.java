package rest.admin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entity.users.Status;
import entity.users.partner.Platform;
import entity.users.user.Role;
import exception.BadRequestException;
import exception.NotFoundException;
import rest.Roles;
import rest.statistics.dto.DetailStatisticsDto;
import rest.statistics.dto.ShortStatisticsDto;
import rest.users.authentication.Secured;
import service.PlatformService;
import service.StatisticsService;
import util.FieldsExclusionStrategy;
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
public class PlatformResource {

    public class PlatformDto {
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
    @Path("{uid}/platforms")
    @Produces(MediaType.APPLICATION_JSON)
    public Response readAll(@PathParam("uid") long userId){
        try{
            List<Platform> platforms = PlatformService.getAllByUserId(userId);
            Gson dOut = new GsonBuilder()
                    .setPrettyPrinting()
                    .setExclusionStrategies(new FieldsExclusionStrategy("partner"))
                    .create();
            return Response.ok(dOut.toJson(platforms)).build();
        } catch (NotFoundException e){
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("{uid}/platforms/{pid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response read(@PathParam("uid") long userId, @PathParam("pid") long platformId){
        try{
            Platform platform = PlatformService.getWithChecking(userId, platformId);
            Gson dOut = new GsonBuilder()
                    .setPrettyPrinting()
                    .setExclusionStrategies(new FieldsExclusionStrategy("partner"))
                    .create();
            return Response.ok(dOut.toJson(platform)).build();
        } catch (NotFoundException e){
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PUT
    @Path("{uid}/platforms/{pid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
            @PathParam("uid") long userId,
            @PathParam("pid") long platformId,
            String content
    ){
        try{
            Gson gson = JsonHelper.getGson();
            PlatformDto platformDto = gson.fromJson(content, PlatformDto.class);
            if (platformDto == null)
                return Response.status(Response.Status.BAD_REQUEST).build();

            Platform platformFromBase = PlatformService.updateExcludeNullWithChecking(userId, platformId, platformDto);
            return Response.ok(gson.toJson(platformFromBase)).build();
        } catch (OptimisticLockException | NotFoundException e){
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DELETE
    @Path("{uid}/platforms/{pid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(
            @PathParam("uid") long userId,
            @PathParam("pid") long platformId
    ){
        try{
            PlatformService.delete(userId, platformId);
            return Response.noContent().build();
        } catch (IllegalArgumentException | OptimisticLockException | NotFoundException e){
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("{uid}/platforms/statistics")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatistics(
            @PathParam("uid") long userId,
            @QueryParam("from") String from,
            @QueryParam("to") String to){
        try{
            ShortStatisticsDto shortStatisticsDto
                    = StatisticsService.getShortPlatformStatistics(userId, from, to);
            return Response.ok(JsonHelper.getGson().toJson(shortStatisticsDto)).build();
        } catch (exception.BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("{uid}/platforms/{pid}/statistics ")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatistics(
            @PathParam("uid") long userId,
            @PathParam("pid") long platformId,
            @QueryParam("from") String from,
            @QueryParam("to") String to,
            @QueryParam("group") String group){

        try{
            DetailStatisticsDto detailStatisticsDto
                    = StatisticsService.getDetailPlatformStatistics(userId, platformId, from, to, group);
            return Response.ok(JsonHelper.getGson().toJson(detailStatisticsDto)).build();
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

}
