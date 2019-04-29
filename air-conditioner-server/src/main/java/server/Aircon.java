package server;

public class Aircon
{
    private final String id;
    private float temperature;
    
    public Aircon(String id, float temperature)
    {
        this.id = id;
        this.temperature = temperature;
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
}
