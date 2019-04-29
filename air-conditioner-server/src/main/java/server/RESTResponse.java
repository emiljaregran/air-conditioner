package server;

import java.util.ArrayList;
import java.util.List;

public class RESTResponse
{
    List<Aircon> aircons = null;
    List<Error> errors = null;
    Float temperature = null;
    
    public RESTResponse(List<Aircon> aircons)
    {
        this.aircons = aircons;
    }
    
    public RESTResponse(Error error)
    {
        errors = new ArrayList<>();
        errors.add(error);
    }
    
    public RESTResponse(float temperature)
    {
        this.temperature = temperature;
    }
}
