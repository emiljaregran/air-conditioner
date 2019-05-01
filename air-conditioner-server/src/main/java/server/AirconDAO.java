package server;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class AirconDAO implements IAirconDAO
{
    private final List<Aircon> aircons = new ArrayList<>();
    
    public AirconDAO()
    {
        aircons.add(new Aircon("A", 12.2f, "°C", 10, 1.3f, "SEK", "2019-05-01 13:00"));
        aircons.add(new Aircon("B", 23.1f, "°C", 20, 1.1f, "SEK", "2019-05-01 13:00"));
        aircons.add(new Aircon("C", 88.9f, "°C", 30, 1.4f, "SEK", "2019-05-01 13:00"));
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
    
    @Override
    public void updateAircon(Aircon updatedAircon)
    {
        for (Aircon aircon : aircons)
        {
            if (aircon.getId().equals(updatedAircon.getId()))
            {
                aircon = updatedAircon;
            }
        }
    }
    
    @Override
    public TemperatureSummary getTemperatureSummary(String id)
    {
        TemperatureSummary temperatureSummary = null;
        
        for (Aircon aircon : aircons)
        {
            if (aircon.getId().equals(id))
            {
                LinkedHashMap<Integer, Float> temperatures = new LinkedHashMap<>();
                temperatureSummary = new TemperatureSummary(temperatures,
                                                            1.0f, 2.0f, 3.0f);
            }
        }
        
        return temperatureSummary;
    }
    
    @Override
    public ElectricitySummary getElectricitySummary(String id)
    {
        ElectricitySummary electricitySummary = null;
        
        for (Aircon aircon : aircons)
        {
            if (aircon.getId().equals(id))
            {
                electricitySummary = new ElectricitySummary(233, 1678.2f,
                    "SEK", 2344, 9540, 1234, 16, 2);
                
            }
        }
        
        return electricitySummary;
    }
    
    @Override
    public String getHighestPowerConsumptionAircon()
    {
        return "B";
    }
}
