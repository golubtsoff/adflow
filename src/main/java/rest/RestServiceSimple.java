package rest;

import rest.users.autentication.Secured;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Deprecated
@Secured
@Path("/persons")
public class RestServiceSimple {
    private static List<Person> persons = new ArrayList<>();

    @GET
    @Path("/hello")
    public Response hello() {
        return Response.status(200).entity("hello").build();
    }

    @GET
    @Path("/")
    public List<Person> getPersons() {
        return persons;
    }

    @GET
    @Path("{pid}")
    public Person getPerson(@PathParam("pid") int id) {
        for(Person person : persons){
            if (person.getId() == id)
                return person;
        }
        return null;
    }

    @POST
    @Path("/")
    @Produces("application/xml")
    public void addPerson(@FormParam("pid") int id,
                          @FormParam("pname") String name){
        persons.add(new Person(id, name));
    }

    @PUT
    @Path("{pid}")
    @Consumes("*/xml")
    public void updatePerson(@PathParam("pid") int id,
                             Person person){
        for(Person p : persons){
            if (p.getId() == id){
                p.setName(person.getName());
                return;
            }
        }
    }

    @DELETE
    @Path("{pid}")
    public void deletePerson(@PathParam("pid") int id){
        for(int i = 0; i < persons.size(); i++){
            if (persons.get(i).getId() == id){
                persons.remove(i);
                return;
            }
        }
    }
}