package server;

public class Aircon
{
    private final String id;
    private Float temperature;
    private String temperatureUnit;
    private Integer powerConsumption;
    private Float electricityPrice;
    private String electricityPriceUnit;
    private String lastUpdate;
    
    
    public Aircon(String id, Float temperature, String temperatureUnit,
            Integer powerConsumption, Float electricityPrice,
            String electricityPriceUnit, String lastUpdate)
    {
        this.id = id;
        this.temperature = temperature;
        this.temperatureUnit = temperatureUnit;
        this.powerConsumption = powerConsumption;
        this.electricityPrice = electricityPrice;
        this.electricityPriceUnit = electricityPriceUnit;
        this.lastUpdate = lastUpdate;
    }
    
    public String getId()
    {
        return id;
    }

    public Float getTemperature()
    {
        return temperature;
    }

    public void setTemperature(float temperature)
    {
        this.temperature = temperature;
    }
    
    public Integer getPowerConsumption()
    {
        return powerConsumption;
    }
    
    public void setPowerConsumption(int powerConsumption)
    {
        this.powerConsumption = powerConsumption;
    }
    
    public Float getElectricityPrice()
    {
        return electricityPrice;
    }
    
    public void setElectricityPrice(float electricityPrice)
    {
        this.electricityPrice = electricityPrice;
    }
}
