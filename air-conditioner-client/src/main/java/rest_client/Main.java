package rest_client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import java.util.Scanner;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

public class Main
{
    private final static ClientConfig clientConfig = new DefaultClientConfig();
    private final static Client client = Client.create(clientConfig);
    private final static WebResource service = client.resource(UriBuilder
            .fromUri("http://localhost:20778/air-conditioner-server").build());
    
    private final GsonBuilder builder = new GsonBuilder();
    private final Gson gson = builder.setPrettyPrinting().create();
    
    private Main()
    {
        boolean run = true;
        
        while (run)
        {
            clearScreen();
            printMenu();
            String menuChoice = getUserInput();
            
            if (menuChoice.equals("0") || menuChoice.equals("1") || 
                menuChoice.equals("2") || menuChoice.equals("3") || 
                menuChoice.equals("4") || menuChoice.equals("5") ||
                menuChoice.equals("6") || menuChoice.equals("7") || 
                menuChoice.equals("8"))
            {
                System.out.println("Which aircon?");
            }

            switch (menuChoice)
            {
                case "0":
                    getTemperature(getUserInput());
                    pauseOutput();
                    break;
                    
                case "1":
                    getPowerConsumption(getUserInput());
                    pauseOutput();
                    break;

                case "2":
                    getElectricityPrice(getUserInput());
                    pauseOutput();
                    break;
                    
                case "3":
                    getFullSummary(getUserInput());
                    pauseOutput();
                    break;
                    
                case "4":
                    setTemperature(getUserInput());
                    pauseOutput();
                    break;
                    
                case "5":
                    setPowerConsumption(getUserInput());
                    pauseOutput();
                    break;
                    
                case "6":
                    setElectricityPrice(getUserInput());
                    pauseOutput();
                    break;
                    
                case "7":
                    getTemperatureSummary24h(getUserInput());
                    pauseOutput();
                    break;
                    
                case "8":
                    getElectricitySummary24h(getUserInput());
                    pauseOutput();
                    break;
                    
                case "9":
                    getHighestPowerConsumption24h();
                    pauseOutput();
                    break;

                case "exit":
                    run = false;
                    break;
            } 
        }
        
    }
    
    private String getUserInput()
    {
        String userInput;
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("> ");
        userInput = scanner.next();
        
        return userInput;
    }
    
    private void printMenu()
    {
        System.out.println("\nAircon REST Client");
        System.out.println("------------------");
        System.out.println("0. GET Temperature");
        System.out.println("1. GET Power consumption");
        System.out.println("2. GET Electricity price");
        System.out.println("3. GET Full summary");
        System.out.println("4. SET Temperature");
        System.out.println("5. SET Power consumption");
        System.out.println("6. SET Electricity price");
        System.out.println("7. GET Temperature summary last 24h");
        System.out.println("8. GET Electricity summary last 24h");
        System.out.println("9. GET Highest power consumer 24h\n"); 
    }
    
    private void pauseOutput()
    {
        System.out.println("\nPress enter to continue...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }
    
    private void clearScreen()
    {  
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
    }
    
    private void getTemperature(String airconId)
    {
        String json;
        RESTResponse restResponse = new RESTResponse();
        
        try
        {
           json = service.path("rest/aircons/" + airconId + "/temperature")
                .accept(MediaType.APPLICATION_JSON).get(String.class);
        }
        catch (UniformInterfaceException e)
        {
            restResponse.setCode(e.getResponse().getStatus());
            restResponse.setMessage("Aircon " + airconId + " not found.");
            System.out.println(restResponse.getErrorMessage());
            return;
        }      
        
        restResponse = new Gson().fromJson(json, RESTResponse.class);
        System.out.println(restResponse.getTemperature());
    }
    
    private void getPowerConsumption(String airconId)
    {
        String json;
        RESTResponse restResponse = new RESTResponse();
        
        try
        {
            json = service.path("rest/aircons/" + airconId + "/powerConsumption")
                .accept(MediaType.APPLICATION_JSON).get(String.class);
        }
        catch (UniformInterfaceException e)
        {
            restResponse.setCode(e.getResponse().getStatus());
            restResponse.setMessage("Aircon " + airconId + " not found.");
            System.out.println(restResponse.getErrorMessage());
            return;
        }
        
        restResponse = new Gson().fromJson(json, RESTResponse.class);
        System.out.println(restResponse.getPowerConsumption());
    }
    
    private void getElectricityPrice(String airconId)
    {
        String json;
        RESTResponse restResponse = new RESTResponse();
        
        try
        {
            json = service.path("rest/aircons/" + airconId + "/electricityPrice")
                .accept(MediaType.APPLICATION_JSON).get(String.class);
        }
        catch (UniformInterfaceException e)
        {
            restResponse.setCode(e.getResponse().getStatus());
            restResponse.setMessage("Aircon " + airconId + " not found.");
            System.out.println(restResponse.getErrorMessage());
            return;
        }
        
        restResponse = new Gson().fromJson(json, RESTResponse.class);
        System.out.println(restResponse.getElectricityPrice());
    }
    
    private void getFullSummary(String airconId)
    {
        String json;
        RESTResponse restResponse = new RESTResponse();
        
        try
        {
            json = service.path("rest/aircons/" + airconId)
                .accept(MediaType.APPLICATION_JSON).get(String.class);
        }
        catch (UniformInterfaceException e)
        {
            restResponse.setCode(e.getResponse().getStatus());
            restResponse.setMessage("Aircon " + airconId + " not found.");
            System.out.println(restResponse.getErrorMessage());
            return;
        }
        
        restResponse = new Gson().fromJson(json, RESTResponse.class);
        System.out.println(restResponse.getTemperature());
        System.out.println(restResponse.getPowerConsumption());
        System.out.println(restResponse.getElectricityPrice());
        System.out.println(restResponse.getLastUpdate());
    }
    
    private void setTemperature(String airconId)
    {
        String json;
        RESTRequest restRequest = new RESTRequest();
        RESTResponse restResponse = new RESTResponse();    
        System.out.print("Enter new temperature ");

        try
        {
            restRequest.setTemperature(Double.valueOf(getUserInput()));
            String request = gson.toJson(restRequest);
            json = service.path("rest/aircons/" + airconId + "/temperature").
                    accept(MediaType.APPLICATION_JSON).post(String.class, request);
        }
        catch (NumberFormatException e)
        {
            System.out.println("ERROR: Not a valid number.");
            return;
        }
        catch (UniformInterfaceException e)
        {
            restResponse.setCode(e.getResponse().getStatus());
            restResponse.setMessage("Aircon " + airconId + " not found.");
            System.out.println(restResponse.getErrorMessage());
            return;
        }
        
        restResponse = new Gson().fromJson(json, RESTResponse.class);
        System.out.println(restResponse.getResponse());
    }
    
    private void setPowerConsumption(String airconId)
    {
        String json;
        RESTRequest restRequest = new RESTRequest();
        RESTResponse restResponse = new RESTResponse();    
        System.out.print("Enter new power consumption ");

        try
        {
            restRequest.setPowerConsumption(Integer.valueOf(getUserInput()));
            String request = gson.toJson(restRequest);
            json = service.path("rest/aircons/" + airconId + "/powerConsumption")
                    .accept(MediaType.APPLICATION_JSON).post(String.class, request);
        }
        catch (NumberFormatException e)
        {
            System.out.println("ERROR: Not a valid number.");
            return;
        }
        catch (UniformInterfaceException e)
        {
            restResponse.setCode(e.getResponse().getStatus());
            restResponse.setMessage("Aircon " + airconId + " not found.");
            System.out.println(restResponse.getErrorMessage());
            return;
        }
        
        restResponse = new Gson().fromJson(json, RESTResponse.class);
        System.out.println(restResponse.getResponse());
    }
    
    private void setElectricityPrice(String airconId)
    {
        String json;
        RESTRequest restRequest = new RESTRequest();
        RESTResponse restResponse = new RESTResponse();    
        System.out.print("Enter new electricity price ");

        try
        {
            restRequest.setElectricityPrice(Double.valueOf(getUserInput()));
            String request = gson.toJson(restRequest);
            json = service.path("rest/aircons/" + airconId + "/electricityPrice")
                    .accept(MediaType.APPLICATION_JSON).post(String.class, request);
        }
        catch (NumberFormatException e)
        {
            System.out.println("ERROR: Not a valid number.");
            return;
        }
        catch (UniformInterfaceException e)
        {
            restResponse.setCode(e.getResponse().getStatus());
            restResponse.setMessage("Aircon " + airconId + " not found.");
            System.out.println(restResponse.getErrorMessage());
            return;
        }
        
        restResponse = new Gson().fromJson(json, RESTResponse.class);
        System.out.println(restResponse.getResponse());
    }
    
    private void getTemperatureSummary24h(String airconId)
    {
        String json;
        RESTResponse restResponse = new RESTResponse();
        
        try
        {
            json = service.path("rest/aircons/" + airconId + "/temperatureSummary24h")
                .accept(MediaType.APPLICATION_JSON).get(String.class);
        }
        catch (UniformInterfaceException e)
        {
            restResponse.setCode(e.getResponse().getStatus());
            restResponse.setMessage("Aircon " + airconId + " not found.");
            System.out.println(restResponse.getErrorMessage());
            return;
        }
        
        restResponse = new Gson().fromJson(json, RESTResponse.class);
        System.out.println(restResponse.getTemperatureSummary24h());
    }
    
    private void getElectricitySummary24h(String airconId)
    {
        String json;
        RESTResponse restResponse = new RESTResponse();
        
        try
        {
            json = service.path("rest/aircons/" + airconId + "/electricitySummary24h")
                .accept(MediaType.APPLICATION_JSON).get(String.class);
        }
        catch (UniformInterfaceException e)
        {
            restResponse.setCode(e.getResponse().getStatus());
            restResponse.setMessage("Aircon " + airconId + " not found.");
            System.out.println(restResponse.getErrorMessage());
            return;
        }
        
        restResponse = new Gson().fromJson(json, RESTResponse.class);
        System.out.println(restResponse.getElectricitySummary24h());
    }
    
    private void getHighestPowerConsumption24h()
    {
        String json = service.path("rest/aircons/highestPowerConsumption24h")
                .accept(MediaType.APPLICATION_JSON).get(String.class);
            
        RESTResponse restResponse = new Gson().fromJson(json, RESTResponse.class);
        System.out.println(restResponse.gethighestPowerConsumption24h());
    }
    
    public static void main(String[] args)
    {
        new Main();
    }
}
