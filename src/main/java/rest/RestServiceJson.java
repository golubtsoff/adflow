package rest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.List;

@Path("/json")
public class RestServiceJson {
    private static List<Person> persons = new Persons();

    @GET
    @Path("/")
    public Response getPersons() {
        return Response.ok(persons, MediaType.APPLICATION_JSON_TYPE).build();
    }

    @GET
    @Path("{pid}")
    public Response getPerson(@PathParam("pid") int id) {
        for(Person person : persons){
            if (person.getId() == id)
                return Response.ok(person, MediaType.APPLICATION_JSON_TYPE).build();
        }
        return Response.noContent().build();
    }

    @POST
    @Path("/")
    public Response addPerson(@FormParam("pid") int id,
                          @FormParam("pname") String name){
        persons.add(new Person(id, name));
        return Response
                .created(UriBuilder
                        .fromResource(RestServiceJson.class)
                        .path(String.valueOf(id))
                        .build())
                .build();
    }

    // At Header add Content-Type as application/json
    @PUT
    @Path("{pid}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePerson(@PathParam("pid") int id,
                             Person person){
        for(Person p : persons){
            if (p.getId() == id){
                p.setName(person.getName());
                return Response.ok(p, MediaType.APPLICATION_JSON_TYPE).build();
            }
        }
        return Response.notModified().build();
    }

    @DELETE
    @Path("{pid}")
    public Response deletePerson(@PathParam("pid") int id){
        for(int i = 0; i < persons.size(); i++){
            if (persons.get(i).getId() == id){
                persons.remove(i);
                break;
            }
        }
        return Response.noContent().build();
    }
}