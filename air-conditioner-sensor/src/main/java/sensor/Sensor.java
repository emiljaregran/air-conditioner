package sensor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.jersey.api.client.*;
import com.sun.jersey.api.client.config.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;


public class Sensor 
{
    private final static ClientConfig clientConfig = new DefaultClientConfig();
    private final static Client client = Client.create(clientConfig);
    private final static WebResource service = client.resource(UriBuilder
            .fromUri("http://localhost:20778/air-conditioner-server").build());
    
    private final GsonBuilder builder = new GsonBuilder();
    private final Gson gson = builder.setPrettyPrinting().create();
    
    private Sensor(String name)
    {  
        double temperature = getRandomTemperature();        
        int powerConsumption = (int)((temperature - 20.0) * 1324);
        
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	Date date = new Date();
        
	System.out.println("[" + dateFormat.format(date) + "]  Aircon " + name + 
                        "  " + temperature + " °C  " + powerConsumption + " W");
        System.out.println(sendTemperature(name, temperature));
        System.out.println(sendPowerConsumption(name, powerConsumption));
    }
    
    private double getRandomTemperature()
    {
        double temperature = 20.0f + getRandomDoubleBetweenRange(5.0, 10.0);
        temperature = Math.round(temperature * 10.0) / 10.0;
        
        return temperature;
    }
    
    private double getRandomDoubleBetweenRange(double min, double max)
    {
        double number = (Math.random() * ((max - min) + 1)) + min;
        return number;
    }
    
    private String sendTemperature(String name, double temperature)
    {
        String temperatureJson = gson.toJson(new Data(temperature));      
        ClientResponse clientResponse = service
                .path("rest/aircons/" + name + "/temperature")
                .accept(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, temperatureJson);
        String responseJson = clientResponse.getEntity(String.class);

        return parseResponse(responseJson);
    }
    
    private String sendPowerConsumption(String name, int powerConsumption)
    {
        String powerConsumptionJson = gson.toJson(new Data(powerConsumption));      
        ClientResponse clientResponse = service
                .path("rest/aircons/" + name + "/powerConsumption")
                .accept(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, powerConsumptionJson);
        String responseJson = clientResponse.getEntity(String.class);

        return parseResponse(responseJson);
    }
    
    private String parseResponse(String json)
    {
        String responseString = null;
        JsonParser parser = new JsonParser();
        JsonObject rootObj;
        rootObj = parser.parse(json).getAsJsonObject();
        JsonArray response = rootObj.getAsJsonArray("response");
        
        for (JsonElement element : response)
        {
            JsonObject responseObject = element.getAsJsonObject();
            String code = responseObject.get("code").getAsString();
            String message = responseObject.get("message").getAsString();
            
            responseString = "(" + code + ") " + message;
        }
        
        return responseString;
    }

    public static void main(String[] args)
    {
        String name = null;
        
        if (args.length == 1)
        {
            if (args[0].equalsIgnoreCase("A") || 
                args[0].equalsIgnoreCase("B") ||
                args[0].equalsIgnoreCase("C"))
            {
                name = args[0];
            }
        }
        
        if (name == null)
        {
            System.err.println("Valid arguments are: A, B or C.");
            System.exit(1);
        }
        
        new Sensor(name);
    }
}
