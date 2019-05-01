package server;

public class HighestConsumption
{
    private final String id;
    private final double consumedElectricity;
    private final String consumedElectricityUnit = "kWh";
    
    public HighestConsumption(String id, float consumedElectricity)
    {
        this.id = id;
        this.consumedElectricity = Math.round(consumedElectricity * 1000) / 1000.0d;
    }
}
