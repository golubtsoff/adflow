package rest.admin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import entity.users.Status;
import entity.users.user.*;
import exception.DbException;
import exception.NotFoundException;
import rest.Roles;
import rest.users.authentication.Secured;
import service.UserService;
import util.FieldsExclusionStrategy;
import util.JsonHelper;

import javax.persistence.OptimisticLockException;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.Arrays;

@Path("/admin/users")
@Secured
@Roles(Role.ADMIN)
public class UserResource {

    public static final String URI_FIELD_NAME = "uri";

    @Context
    HttpHeaders headers;

    @Context
    private UriInfo uriInfo;

    private class Credential{
        private String login;
        private String password;
    }

    public class UserDto {

        private Person person;
        private Contact contact;
        private Status status;

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
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response readAll(){
        try{
            Gson dOut = new GsonBuilder()
                    .setPrettyPrinting()
                    .setExclusionStrategies(new FieldsExclusionStrategy(
                            "hash", "person", "contact"))
                    .create();
            return Response.ok(dOut.toJson(UserService.getAll())).build();
        } catch (DbException e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("{uid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response read(@PathParam("uid") long id){
        try {
            User user = UserService.get(id);
            if (user == null)
                return Response.status(Response.Status.NOT_FOUND).build();
            return Response.ok(JsonHelper.getJsonStringExcludeFields(
                    user,
                    Arrays.asList("hash")
            )).build();
        } catch (DbException e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAdmin(String content){
        try {
            Gson gson = JsonHelper.getGson();
            Credential credential = gson.fromJson(content, Credential.class);

            User user = UserService.signUp(credential.login.toLowerCase(), credential.password, Role.ADMIN);
            if (user == null)
                return Response.status(Response.Status.FORBIDDEN).build();
            URI userUri = uriInfo.getAbsolutePathBuilder().path(user.getId().toString()).build();

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(URI_FIELD_NAME, userUri.toString());

            return Response.ok(gson.toJson(jsonObject)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @PUT
    @Path("{uid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("uid") long id, String content){
        try{
            Gson gson = JsonHelper.getGson();
            UserDto userDto = gson.fromJson(content, UserDto.class);
            if (userDto == null)
                return Response.status(Response.Status.BAD_REQUEST).build();

            User userFromBase = UserService.updateExcludeNull(id, userDto);
            return Response.ok(JsonHelper.getJsonStringExcludeFields(
                    userFromBase,
                    Arrays.asList("hash")
            )).build();
        } catch (OptimisticLockException | NotFoundException e){
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DELETE
    @Path("{uid}")
    public Response delete(@PathParam("uid") long id){
        try{
            UserService.delete(id);
            return Response.noContent().build();
        } catch (IllegalArgumentException e){
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }


}
