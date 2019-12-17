package rest;

import com.google.gson.Gson;
import entity.users.Account;
import entity.users.user.Role;
import entity.users.user.UserToken;
import exception.NotFoundException;
import rest.users.autentication.Secured;
import service.AccountService;
import util.JsonHelper;

import javax.persistence.OptimisticLockException;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
@Secured
public class AccountResource {

    @Context
    HttpHeaders headers;

    @GET
    @Path("client/account")
    @Roles({Role.CUSTOMER, Role.PARTNER})
    @Produces(MediaType.APPLICATION_JSON)
    public Response read(){
        try {
            long userId = Long.valueOf(headers.getHeaderString(UserToken.UID));
            return getResponseRead(userId);
        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    private Response getResponseRead(long userId){
        try {
            Account account = AccountService.get(userId);
            if (account == null)
                return Response.status(Response.Status.NOT_FOUND).build();
            return Response.ok(JsonHelper.getGson().toJson(account)).build();
        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PUT
    @Path("/client/account")
    @Roles({Role.CUSTOMER, Role.PARTNER})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(String content){
        try{
            long userId = Long.valueOf(headers.getHeaderString(UserToken.UID));
            return getResponseUpdate(userId, content);
        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    private Response getResponseUpdate(long userId, String content){
        try{
            Gson gson = JsonHelper.getGson();
            Account accountFromClient = gson.fromJson(content, Account.class);
            if (accountFromClient == null)
                return Response.status(Response.Status.BAD_REQUEST).build();

            Account accountFromBase = AccountService.updateExcludeNull(userId, accountFromClient);
            return Response.ok(JsonHelper.getGson().toJson(accountFromBase)).build();
        } catch (OptimisticLockException | NotFoundException e){
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("/admin/client/{uid}/account")
    @Roles(Role.ADMIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response read(@PathParam("uid") long userId){
        return getResponseRead(userId);
    }

    @PUT
    @Path("/admin/client/{uid}/account")
    @Roles(Role.ADMIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("uid") long userId, String content){
        return getResponseUpdate(userId, content);
    }

}
