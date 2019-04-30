package sensor;

public class Data
{
    private Double temperature = null;
    private Integer powerConsumption = null;
    
    public Data(double temperature)
    {
        this.temperature = temperature;
    }
    
    public Data(int powerConsumption)
    {
        this.powerConsumption = powerConsumption;
    }
}
