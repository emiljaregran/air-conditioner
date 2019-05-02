package rest_client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import java.util.Scanner;
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
            printMenu();
            String menuChoice = getUserInput();

            switch (menuChoice)
            {
                case "0":
                    break;
                    
                case "1":
                    break;

                case "2":
                    break;
                    
                case "3":
                    break;
                    
                case "4":
                    break;
                    
                case "5":
                    break;
                    
                case "6":
                    break;
                    
                case "7":
                    break;
                    
                case "8":
                    break;
                    
                case "9":
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
        System.out.println("1. GET Electricity consumption");
        System.out.println("2. GET Electricity price");
        System.out.println("3. GET Full summary\n");
        System.out.println("4. SET Temperature");
        System.out.println("5. SET Electricity consumption");
        System.out.println("6. SET Electricity price\n");
        System.out.println("7. GET Temperature summary last 24h");
        System.out.println("8. GET Electricity summary last 24h");
        System.out.println("9. GET Highest electricity consumer 24h\n"); 
    }
    
    public static void main(String[] args)
    {
        new Main();
    }
}
