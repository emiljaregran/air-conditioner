package rest_client;

public class RESTRequest
{
    private Double temperature = null;
    private Integer powerConsumption = null;
    private Double electricityPrice = null;
    
    public void setTemperature(double temperature)
    {
        this.temperature = temperature;
    }
    
    public void setPowerConsumption(int powerConsumption)
    {
        this.powerConsumption = powerConsumption;
    }
    
    public void setElectricityPrice(double electricityPrice)
    {
        this.electricityPrice = electricityPrice;
    }
}
