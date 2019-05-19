package rest.admin;

import com.google.gson.Gson;
import entity.users.Account;
import entity.users.Status;
import entity.users.user.Contact;
import entity.users.user.Person;
import entity.users.user.Role;
import entity.users.user.User;
import exception.DbException;
import rest.Roles;
import rest.users.autentication.Secured;
import service.AccountService;
import service.UserService;
import util.JsonHelper;

import javax.persistence.OptimisticLockException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;

@Path("/admin/users")
@Secured
@Roles(Role.ADMIN)
public class AccountResource {

    @GET
    @Path("{uid}/account")
    @Produces(MediaType.APPLICATION_JSON)
    public Response read(@PathParam("uid") long userId){
        try {
            Account account = AccountService.get(userId);
            if (account == null)
                return Response.status(Response.Status.NOT_FOUND).build();
            return Response.ok(JsonHelper.getGson().toJson(account)).build();
        } catch (DbException e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PUT
    @Path("{uid}/account")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("uid") long userId, String content){
        try{
            Gson gson = JsonHelper.getGson();
            Account accountFromClient = gson.fromJson(content, Account.class);
            if (accountFromClient == null)
                return Response.notModified().build();

            Account accountFromBase = AccountService.get(userId);
            if (accountFromBase == null)
                return Response.status(Response.Status.NOT_FOUND).build();

            accountFromBase = partlyUpdateAccount(accountFromClient, accountFromBase);
            AccountService.update(userId, accountFromBase);

            return Response.ok(JsonHelper.getGson().toJson(accountFromBase)).build();
        } catch (DbException e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (OptimisticLockException e){
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception e){
            return Response.notModified().build();
        }
    }

    private Account partlyUpdateAccount(Account accountFromClient, Account accountFromBase){
        BigDecimal balance = accountFromClient.getBalance();
        String paymentDetails = accountFromClient.getPaymentDetails();
        if (balance != null)
            accountFromBase.setBalance(balance);
        if (paymentDetails != null)
            accountFromBase.setPaymentDetails(paymentDetails);
        return accountFromBase;
    }

}
