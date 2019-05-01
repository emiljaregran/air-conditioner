package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
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
    private final GsonBuilder builder = new GsonBuilder();
    private final Gson gson = builder.setPrettyPrinting().create();
    
    //private final static IAirconDAO DAO = new AirconDAO();
    private final static IAirconDAO DAO = new AirconDBDAO();
            
    @GET
    @Path("/")
    @Produces (MediaType.APPLICATION_JSON)
    public Response getAllAircons()
    {
        RESTResponse restResponse = new RESTResponse();
        restResponse.setAircons(DAO.getAllAircons());
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
            return airconNotFound(id);
        }
        
        return Response.ok(gson.toJson(aircon)).build();
    }
    
    @GET
    @Path("/{id}/temperature")
    @Produces (MediaType.APPLICATION_JSON)
    public Response getTemperature(@PathParam("id") String id)
    {
        Aircon aircon = DAO.getAirconById(id);
        
        if (aircon == null)
        {
            return airconNotFound(id);
        }
        
        RESTResponse restResponse = new RESTResponse();
        restResponse.setTemperature(aircon.getTemperature());
        return Response.ok(gson.toJson(restResponse)).build();
    }
    
    @POST
    @Path("/{id}/temperature")
    @Produces (MediaType.APPLICATION_JSON)
    public Response setTemperature(@PathParam("id") String id, String json)
    {
        Aircon aircon = DAO.getAirconById(id);
        JsonParser parser = new JsonParser();
        JsonObject rootObj;
        float temperature;
        
        if (aircon == null)
        {
            return airconNotFound(id);
        }

        try
        {
            rootObj = parser.parse(json).getAsJsonObject();
            temperature = rootObj.get("temperature").getAsFloat();          
        }
        catch (JsonSyntaxException | NullPointerException e)
        {
            return wrongJsonFormat("temperature");
        }
        
        aircon.setTemperature(temperature);
        DAO.updateAircon(aircon);
        
        ResponseBody updated = new ResponseBody(200, "Temperature updated successfully.");
        RESTResponse restResponse = new RESTResponse();
        restResponse.setResponse(updated);
        return Response.ok(gson.toJson(restResponse)).build();
    }
    
    @GET
    @Path("/{id}/powerConsumption")
    @Produces (MediaType.APPLICATION_JSON)
    public Response getPowerConsumption(@PathParam("id") String id)
    {
        Aircon aircon = DAO.getAirconById(id);
        
        if (aircon == null)
        {
            return airconNotFound(id);
        }
        
        RESTResponse restResponse = new RESTResponse();
        restResponse.setPowerConsumption(aircon.getPowerConsumption());
        return Response.ok(gson.toJson(restResponse)).build();
    }
    
    @POST
    @Path("/{id}/powerConsumption")
    @Produces (MediaType.APPLICATION_JSON)
    public Response setPowerConsumption(@PathParam("id") String id, String json)
    {
        Aircon aircon = DAO.getAirconById(id);
        JsonParser parser = new JsonParser();
        JsonObject rootObj;
        int powerConsumption;
        
        if (aircon == null)
        {
            return airconNotFound(id);
        }

        try
        {
            rootObj = parser.parse(json).getAsJsonObject();
            powerConsumption = rootObj.get("powerConsumption").getAsInt();          
        }
        catch (JsonSyntaxException | NullPointerException e)
        {
            return wrongJsonFormat("powerConsumption");
        }
        
        aircon.setPowerConsumption(powerConsumption);
        DAO.updateAircon(aircon);
        
        ResponseBody updated = new ResponseBody(200, "Power consumption updated successfully.");
        RESTResponse restResponse = new RESTResponse();
        restResponse.setResponse(updated);
        return Response.ok(gson.toJson(restResponse)).build();
    }
    
    @GET
    @Path("/{id}/electricityPrice")
    @Produces (MediaType.APPLICATION_JSON)
    public Response getElectricityPrice(@PathParam("id") String id)
    {
        Aircon aircon = DAO.getAirconById(id);
        
        if (aircon == null)
        {
            return airconNotFound(id);
        }
        
        RESTResponse restResponse = new RESTResponse();
        restResponse.setElectricityPrice(aircon.getElectricityPrice());
        return Response.ok(gson.toJson(restResponse)).build();
    }
    
    @POST
    @Path("/{id}/electricityPrice")
    @Produces (MediaType.APPLICATION_JSON)
    public Response setElectricityPrice(@PathParam("id") String id, String json)
    {
        Aircon aircon = DAO.getAirconById(id);
        JsonParser parser = new JsonParser();
        JsonObject rootObj;
        float electricityPrice;
        
        if (aircon == null)
        {
            return airconNotFound(id);
        }

        try
        {
            rootObj = parser.parse(json).getAsJsonObject();
            electricityPrice = rootObj.get("electricityPrice").getAsFloat();          
        }
        catch (JsonSyntaxException | NullPointerException e)
        {
            return wrongJsonFormat("electricityPrice");
        }
        
        aircon.setElectricityPrice(electricityPrice);
        DAO.updateAircon(aircon);
        
        ResponseBody updated = new ResponseBody(200, "Electricity price updated successfully.");
        RESTResponse restResponse = new RESTResponse();
        restResponse.setResponse(updated);
        return Response.ok(gson.toJson(restResponse)).build();
    }
    
    @GET
    @Path("/{id}/temperatureSummary24h")
    @Produces (MediaType.APPLICATION_JSON)
    public Response getTemperatureSummary(@PathParam("id") String id)
    {
        TemperatureSummary temperatureSummary = DAO.getTemperatureSummary(id);
        
        if (temperatureSummary == null)
        {
            return airconNotFound(id);
        }
        
        return Response.ok(gson.toJson(temperatureSummary)).build();
    }

    @GET
    @Path("/{id}/electricitySummary24h")
    @Produces (MediaType.APPLICATION_JSON)
    public Response getElectricitySummary(@PathParam("id") String id)
    {
        ElectricitySummary electricitySummary = DAO.getElectricitySummary(id);
        
        if (electricitySummary == null)
        {
            return airconNotFound(id);
        }
        
        return Response.ok(gson.toJson(electricitySummary)).build();
    }
    
    @GET
    @Path("/highestPowerConsumption")
    @Produces (MediaType.APPLICATION_JSON)
    public Response getHighestPowerConsumptionAircon()
    {
        HighestConsumption highestConsumption = DAO.getHighestPowerConsumptionAircon();
        return Response.ok(gson.toJson(highestConsumption)).build();
    }
    
    private Response wrongJsonFormat(String parameter)
    {
        ResponseBody error = new ResponseBody(400, "Wrong format. Use {\"" + parameter + "\": value}");
        RESTResponse restResponse = new RESTResponse();
        restResponse.setResponse(error);
        return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(restResponse)).build();
    }
    
    private Response airconNotFound(String id)
    {
        ResponseBody error = new ResponseBody(404, "Sorry, Aircon " + id + " not found in the database.");
        RESTResponse restResponse = new RESTResponse();
        restResponse.setResponse(error);
        return Response.status(Response.Status.NOT_FOUND).entity(gson.toJson(restResponse)).build();
    }
}
