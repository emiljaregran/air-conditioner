package server;

import java.util.HashMap;
import java.util.Map;

public class TemperatureSummary
{
    private final Map<Integer, Float> temperatures = new HashMap<>();
    private Float averageTemperature = null;
    private Float maxTemperature = null;
    private Float minTemperature = null;
    
    public void addTemperature(int hour, float temperature)
    {
        temperatures.put(hour, temperature);
    }
    
    public void setAverageTemperature(float averageTemperature)
    {
        this.averageTemperature = averageTemperature;
    }
    
    public void setMaxTemperature(float maxTemperature)
    {
        this.maxTemperature = maxTemperature;
    }
    
    public void setMinTemperature(float minTemperature)
    {
        this.minTemperature = minTemperature;
    }
}
