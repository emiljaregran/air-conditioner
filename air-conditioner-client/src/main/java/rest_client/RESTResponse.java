package rest_client;

import java.util.List;

public class RESTResponse 
{
    private int code;
    private float temperature;
    private String temperatureUnit;
    private int powerConsumption;
    private float electricityPrice;
    private String electricityPriceUnit;
    private String lastUpdate;
    private String message;
    List<ResponseBody> response = null;
    
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
        return "Code: " + code + " Message: " + message;
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
    
    public String getErrorMessage()
    {
        return "(" + code + ") " + message;
    }
}
