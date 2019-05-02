package server;

import java.util.ArrayList;
import java.util.List;

public class RESTResponse
{
    List<Aircon> aircons = null;
    List<ResponseBody> response = null;
    Float temperature = null;
    String temperatureUnit = null;
    Integer powerConsumption = null;
    Float electricityPrice = null;
    String electricityPriceUnit = null;
    String id = null;
    
    public void setAircons(List<Aircon> aircons)
    {
        this.aircons = aircons;
    }
    
    public void setResponse(ResponseBody response)
    {
        this.response = new ArrayList<>();
        this.response.add(response);
    }
    
    public void setTemperature(float temperature)
    {
        this.temperature = temperature;
    }
    
    public void setTemperatureUnit(String temperatureUnit)
    {
        this.temperatureUnit = temperatureUnit;
    }
    
    public void setPowerConsumption(int powerConsumption)
    {
        this.powerConsumption = powerConsumption;
    }
    
    public void setElectricityPrice(float electricityPrice)
    {
        this.electricityPrice = electricityPrice;
    }
    
    public void setElectricityPriceUnit(String electricityPriceUnit)
    {
        this.electricityPriceUnit = electricityPriceUnit;
    }
}
