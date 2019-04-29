package server;

import java.util.ArrayList;
import java.util.List;

public class RESTResponse
{
    List<Aircon> aircons = null;
    List<ResponseBody> response = null;
    Float temperature = null;
    Integer powerConsumption = null;
    
    public RESTResponse(List<Aircon> aircons)
    {
        this.aircons = aircons;
    }
    
    public RESTResponse(ResponseBody response)
    {
        this.response = new ArrayList<>();
        this.response.add(response);
    }
    
    public RESTResponse(float temperature)
    {
        this.temperature = temperature;
    }
    
    public RESTResponse(int powerConsumption)
    {
        this.powerConsumption = powerConsumption;
    }
}
