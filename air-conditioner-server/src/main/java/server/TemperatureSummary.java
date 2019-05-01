package server;

import java.util.LinkedHashMap;

public class TemperatureSummary
{
    private LinkedHashMap<Integer, Float> temperatures;
    private double averageTemperature;
    private double maxTemperature;
    private double minTemperature;
    
    public TemperatureSummary(LinkedHashMap<Integer, Float> temperatures,
            float averageTemperature, float maxTemperature,
            float minTemperature)
    {
        this.temperatures = temperatures;
        this.averageTemperature = Math.round(averageTemperature * 10) / 10.0d;
        this.maxTemperature = Math.round(maxTemperature * 10) / 10.0d;
        this.minTemperature = Math.round(minTemperature * 10) / 10.0d;
    }
}
