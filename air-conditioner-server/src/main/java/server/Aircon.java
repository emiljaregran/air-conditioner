package server;

public class Aircon
{
    private final String id;
    private float temperature;
    private int powerConsumption;
    private float electricityPrice;
    
    
    public Aircon(String id, float temperature, int powerConsumption, float electricityPrice)
    {
        this.id = id;
        this.temperature = temperature;
        this.powerConsumption = powerConsumption;
        this.electricityPrice = electricityPrice;
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
    
    public float getElectricityPrice()
    {
        return electricityPrice;
    }
    
    public void setElectricityPrice(float electricityPrice)
    {
        this.electricityPrice = electricityPrice;
    }
}
