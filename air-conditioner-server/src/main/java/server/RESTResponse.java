package server;

import java.util.ArrayList;
import java.util.List;

public class RESTResponse
{
    List<Aircon> aircons = null;
    List<ResponseBody> response = null;
    Float temperature = null;
    Integer powerConsumption = null;
    Float electricityPrice = null;
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
    
    public void setPowerConsumption(int powerConsumption)
    {
        this.powerConsumption = powerConsumption;
    }
    
    public void setElectricityPrice(float electricityPrice)
    {
        this.electricityPrice = electricityPrice;
    }
    
    public void setHighestPowerConsumptionAircon(String id)
    {
        this.id = id;
    }
}
