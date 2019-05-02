package rest_client;

import java.util.LinkedHashMap;
import java.util.List;

public class RESTResponse 
{
    private int code;
    private float temperature;
    private float averageTemperature;
    private float maxTemperature;
    private float minTemperature;
    private String temperatureUnit;
    private final LinkedHashMap<Integer, Float> temperatures = new LinkedHashMap<>();
    private int powerConsumption;
    private float electricityPrice;
    private String electricityPriceUnit;
    private String lastUpdate;
    private String message;
    private final List<ResponseBody> response = null;
    
    
    public void setCode(int code)
    {
        this.code = code;
    }
    
    public void setMessage(String message)
    {
        this.message = message;
    }
    
    public String getResponse()
    {
        return "(" + response.get(0).getCode() + ") "
                + response.get(0).getMessage();
    }
    
    public String getTemperature()
    {
        return "Temperature: " + temperature + " " + temperatureUnit;
    }
    
    public String getPowerConsumption()
    {
        return "Power consumption: " + powerConsumption + " W";
    }
    
    public String getElectricityPrice()
    {
        return "Electricity price: " + electricityPrice + " " + electricityPriceUnit;
    }
    
    public String getLastUpdate()
    {
        return "Last updated: " + lastUpdate;
    }
    
    public String getTemperatureSummary24h()
    {
        StringBuilder stringBuilder = new StringBuilder();
        
        temperatures.forEach((hour, temp) -> {
            stringBuilder.append("\n");
            stringBuilder.append(hour);
            stringBuilder.append(":00\t");
            stringBuilder.append(temp);
            stringBuilder.append(" ");
            stringBuilder.append(temperatureUnit);
        });
        
        stringBuilder.append("\n\nAverage temperature:\t").append(averageTemperature).append(" ").append(temperatureUnit);
        stringBuilder.append("\nMax temperature:\t").append(maxTemperature).append(" ").append(temperatureUnit);
        stringBuilder.append("\nMin temperature:\t").append(minTemperature).append(" ").append(temperatureUnit);
        
        return stringBuilder.toString();
    }
    
    public String getErrorMessage()
    {
        return "(" + code + ") " + message;
    }
}
