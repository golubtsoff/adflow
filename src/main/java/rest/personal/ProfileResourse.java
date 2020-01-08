package rest.personal;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import entity.users.Status;
import entity.users.user.Contact;
import entity.users.user.Person;
import entity.users.user.User;
import entity.users.user.UserToken;
import exception.DbException;
import exception.NotFoundException;
import rest.users.authentication.Secured;
import service.UserService;
import util.Hash;
import util.JsonHelper;

import javax.persistence.OptimisticLockException;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;

@Path("/profile")
@Secured
public class ProfileResourse {

    @Context
    HttpHeaders headers;

    public class UserDto {
        private String hash;
        private Person person;
        private Contact contact;
        private Status status;

        public String getHash() {
            return hash;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        public Person getPerson() {
            return person;
        }

        public void setPerson(Person person) {
            this.person = person;
        }

        public Contact getContact() {
            return contact;
        }

        public void setContact(Contact contact) {
            this.contact = contact;
        }

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(){
        try {
            long userId = Long.parseLong(headers.getHeaderString(UserToken.UID));
            User user = UserService.get(userId);
            return Response.ok(JsonHelper.getJsonStringExcludeFields(
                    user,
                    Arrays.asList("hash", "status"))
            ).build();
        } catch (DbException | ClassCastException e){
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e){
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(String content){
        try{
            long userId = Long.parseLong(headers.getHeaderString(UserToken.UID));
            Gson gson = JsonHelper.getGson();
            UserDto userDto = gson.fromJson(content, UserDto.class);
            if (userDto == null)
                return Response.status(Response.Status.BAD_REQUEST).build();

            UserDto userToUpdate = new UserDto();
            userToUpdate.setContact(userDto.getContact());
            userToUpdate.setPerson(userDto.getPerson());

            User userFromBase = UserService.updateExcludeNull(userId, userToUpdate);

            return Response.ok(JsonHelper.getJsonStringExcludeFields(
                    userFromBase,
                    Arrays.asList("hash", "status"))
            ).build();
        } catch (OptimisticLockException | NotFoundException e){
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DELETE
    public Response deleteUser(){
        try{
            long userId = Long.parseLong(headers.getHeaderString(UserToken.UID));
            UserDto userToUpdate = new UserDto();
            userToUpdate.setStatus(Status.REMOVED);
            UserService.updateExcludeNull(userId, userToUpdate);
            return Response.noContent().build();
        } catch (IllegalArgumentException | NotFoundException e){
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PUT
    @Path("/password")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePassword(String content){
        try{
            JsonObject jsonObject = new JsonParser().parse(content).getAsJsonObject();
            String password = jsonObject.get("password").getAsString();
            String hash = Hash.getHash(password);

            long userId = Long.parseLong(headers.getHeaderString(UserToken.UID));
            UserDto userDto = new UserDto();
            userDto.setHash(hash);
            UserService.updateExcludeNull(userId, userDto);

            return Response.ok().build();
        } catch (NotFoundException e){
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

}
