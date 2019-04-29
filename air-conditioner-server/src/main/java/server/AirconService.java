package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
@Path("/aircons")

public class AirconService
{
    private final static IAirconDAO DAO = new AirconDAO();
    private final GsonBuilder builder = new GsonBuilder();
    private final Gson gson = builder.setPrettyPrinting().create();
            
    @GET
    @Path("/")
    @Produces (MediaType.APPLICATION_JSON)
    public Response getAllAircons()
    {
        RESTResponse restResponse = new RESTResponse(DAO.getAllAircons());
        return Response.ok(gson.toJson(restResponse)).build();
    }
    
    @GET
    @Path("/{id}")
    @Produces (MediaType.APPLICATION_JSON)
    public Response getAirconById(@PathParam("id") String id)
    {
        Aircon aircon = DAO.getAirconById(id);
        
        if (aircon == null)
        {
            Error error = new Error(404, "Sorry, Aircon " + id + " not found in the database.");
            RESTResponse restResponse = new RESTResponse(error);
            return Response.status(Response.Status.NOT_FOUND).entity(gson.toJson(restResponse)).build();
        }
        
        return Response.ok(gson.toJson(aircon)).build();
    }
    
    @GET
    @Path("/{id}/temperature")
    @Produces (MediaType.APPLICATION_JSON)
    public Response getTemperature(@PathParam("id") String id)
    {
        RESTResponse restResponse;
        Aircon aircon = DAO.getAirconById(id);
        
        if (aircon == null)
        {
            Error error = new Error(404, "Sorry, Aircon " + id + " not found in the database.");
            restResponse = new RESTResponse(error);
            return Response.status(Response.Status.NOT_FOUND).entity(gson.toJson(restResponse)).build();
        }
        
        restResponse = new RESTResponse(aircon.getTemperature());
        return Response.ok(gson.toJson(restResponse)).build();
    }
}
