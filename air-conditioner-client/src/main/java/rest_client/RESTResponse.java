package rest_client;

import java.util.LinkedHashMap;
import java.util.List;

public class RESTResponse 
{
    private String id;
    private int code;
    private float temperature;
    private float averageTemperature;
    private float maxTemperature;
    private float minTemperature;
    private String temperatureUnit;
    private final LinkedHashMap<Integer, Float> temperatures = new LinkedHashMap<>();
    private int powerConsumption;
    
    private float consumedElectricity;
    private String consumedElectricityUnit;
    private float electricityCost;
    private int averagePowerConsumption;
    private int maxPowerConsumption;
    private int minPowerConsumption;
    private int highestElectricityPriceHour;
    private int lowestElectricityPriceHour;
    
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
        return "Electricity price: " + electricityPrice + " " 
                + electricityPriceUnit + "/kWh";
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
        
        stringBuilder.append("\n\nAverage temperature:\t").
                append(averageTemperature).append(" ").append(temperatureUnit);
        stringBuilder.append("\nMax temperature:\t").append(maxTemperature).
                append(" ").append(temperatureUnit);
        stringBuilder.append("\nMin temperature:\t").append(minTemperature).
                append(" ").append(temperatureUnit);
        
        return stringBuilder.toString();
    }
    
    public String getElectricitySummary24h()
    {
        StringBuilder stringBuilder = new StringBuilder();
        
        stringBuilder.append("\nConsumed electricity:\t\t").
                append(consumedElectricity).append("\t").
                append(consumedElectricityUnit); 
        stringBuilder.append("\nElectricity cost:\t\t").
                append(electricityCost).append("\t").
                append(electricityPriceUnit);
        stringBuilder.append("\nAverage power consumption:\t").
                append(averagePowerConsumption).append("\tW");
        stringBuilder.append("\nMax power consumption:\t\t").
                append(maxPowerConsumption).append("\tW");
        stringBuilder.append("\nMin power consumption:\t\t").
                append(minPowerConsumption).append("\tW");
        stringBuilder.append("\nHighest electricity price hour:\t").
                append(highestElectricityPriceHour).append(":00");
        stringBuilder.append("\nLowest electricity price hour:\t").
                append(lowestElectricityPriceHour).append(":00");
        
        return stringBuilder.toString();
    }
    
    public String gethighestPowerConsumption24h()
    {
        StringBuilder stringBuilder = new StringBuilder();
        
        stringBuilder.append("\nAircon:\t\t\t").append(id);
        stringBuilder.append("\nPower consumption:\t").
                append(consumedElectricity).append("\t").
                append(consumedElectricityUnit);
        
        return stringBuilder.toString();
    }
    
    public String getErrorMessage()
    {
        return "(" + code + ") " + message;
    }
}
