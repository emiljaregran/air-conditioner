package price;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

public class Price
{
    private WebClient webClient = new WebClient();
    private final static ClientConfig clientConfig = new DefaultClientConfig();
    private final static Client restClient = Client.create(clientConfig);
    private final static WebResource restService = restClient.resource(UriBuilder
            .fromUri("http://localhost:20778/air-conditioner-server").build());
    
    private final GsonBuilder builder = new GsonBuilder();
    private final Gson gson = builder.setPrettyPrinting().create();
    
    private Price()
    {   
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
        
        double electricityPriceA = getElectricityPrice("https://elen.nu/timpriser-pa-el-for-elomrade-se4-malmo") + 1.0;
        double electricityPriceB = getElectricityPrice("https://elen.nu/timpriser-pa-el-for-elomrade-se3-stockholm") + 1.0;
        double electricityPriceC = getElectricityPrice("https://elen.nu/timpriser-pa-el-for-elomrade-se1-lulea") + 1.0;
        
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	Date date = new Date();
        
        System.out.printf("[%s] Aircon A: %.4f SEK/kWh\n", 
                          dateFormat.format(date), electricityPriceA);
        System.out.println(sendElectricityPrice("A", electricityPriceA) + "\n");
        
        System.out.printf("[%s] Aircon B: %.4f SEK/kWh\n", 
                          dateFormat.format(date), electricityPriceB);
        System.out.println(sendElectricityPrice("B", electricityPriceB) + "\n");
        
        System.out.printf("[%s] Aircon C: %.4f SEK/kWh\n", 
                          dateFormat.format(date), electricityPriceC);
        System.out.println(sendElectricityPrice("C", electricityPriceC) + "\n");
    }
    
    private String sendElectricityPrice(String name, double price)
    {
        String json = gson.toJson(new Data(price));      
        ClientResponse clientResponse = restService
                .path("rest/aircons/" + name + "/electricityPrice")
                .accept(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, json);
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
    
    private double getElectricityPrice(String url)
    {
        HtmlPage page = null;
        Double price = null;
        
        try
        { 
            page = webClient.getPage(url);
        }
        catch (IOException e)
        {
            System.err.println("ERROR: Could not connect to price server.");
            System.exit(-1);
        }
        
        List<HtmlElement> items = page.getByXPath("//div[@class='elspot-area-price']");
        if (items.isEmpty())
        {
            System.out.println("No items found!");
        }
        else
        {
            String[] result = items.get(0).getTextContent().split(" ");
            price = Double.parseDouble(result[0].replaceAll(",", ".")) / 100;
        }
        
        return price;
    }
    
    public static void main(String[] args)
    {
        new Price();
    }
}
