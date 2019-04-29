package server;

public class Aircon
{
    private final String id;
    private float temperature;
    private int powerConsumption;
    
    public Aircon(String id, float temperature, int powerConsumption)
    {
        this.id = id;
        this.temperature = temperature;
        this.powerConsumption = powerConsumption;
    }
    
    public String getId()
    {
        return id;
    }

    public float getTemperature()
    {
        return temperature;
    }

    public void setTemperature(float temperature)
    {
        this.temperature = temperature;
    }
    
    public int getPowerConsumption()
    {
        return powerConsumption;
    }
    
    public void setPowerConsumption(int powerConsumption)
    {
        this.powerConsumption = powerConsumption;
    }
}
