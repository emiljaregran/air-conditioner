package server;

import java.util.ArrayList;
import java.util.List;

public class AirconDAO implements IAirconDAO
{
    private final List<Aircon> aircons = new ArrayList<>();
    
    public AirconDAO()
    {
        aircons.add(new Aircon("A", 12.2f, 10));
        aircons.add(new Aircon("B", 23.1f, 20));
        aircons.add(new Aircon("C", 88.9f, 30));
    }
    
    @Override
    public List<Aircon> getAllAircons()
    {
        return aircons;
    }
    
    @Override
    public Aircon getAirconById(String id)
    {
        Aircon result = null;
        
        for (Aircon aircon : aircons)
        {
            if (aircon.getId().equals(id))
            {
                result = aircon;
            }
        }
        
        return result;
    }
}
