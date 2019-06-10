package rest.admin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entity.users.Status;
import entity.users.partner.Platform;
import entity.users.user.Role;
import exception.NotFoundException;
import rest.Roles;
import rest.admin.strategy.PlatformExclusionStrategy;
import rest.users.autentication.Secured;
import service.PlatformService;
import util.JsonHelper;

import javax.persistence.OptimisticLockException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.Arrays;
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

    // TODO: предусмотреть ограничение по максимальной длине списка. Например, 100 пользователей.
    @GET
    @Path("{uid}/platforms")
    @Produces(MediaType.APPLICATION_JSON)
    public Response readAll(@PathParam("uid") long userId){
        try{
            List<Platform> platforms = PlatformService.getAllByUserId(userId);
            Gson dOut = new GsonBuilder()
                    .setPrettyPrinting()
                    .setExclusionStrategies(new PlatformExclusionStrategy())
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
            return Response.ok(JsonHelper.getJsonStringExcludeFields(
                    platform,
                    Arrays.asList("partner")
            )).build();
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
            PlatformService.deleteWithChecking(userId, platformId);
            return Response.noContent().build();
        } catch (IllegalArgumentException | OptimisticLockException | NotFoundException e){
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

}
