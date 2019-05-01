package server;

import java.util.ArrayList;
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
                temperatureSummary = new TemperatureSummary();
        
                for (int i = 0; i < 24; i++)
                {
                    temperatureSummary.addTemperature(i, i + 2);  
                }

                temperatureSummary.setAverageTemperature(14.4f);
                temperatureSummary.setMaxTemperature(28.2f);
                temperatureSummary.setMinTemperature(2.6f);
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
                electricitySummary = new ElectricitySummary();
                
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
