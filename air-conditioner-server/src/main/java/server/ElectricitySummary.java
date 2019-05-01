package server;

public class ElectricitySummary
{
    private final float consumedElectricity;
    private final String consumedElectricityUnit = "kWh";
    private final double electricityCost;
    private final String electricityPriceUnit;
    private final int averagePowerConsumption;
    private final int maxPowerConsumption;
    private final int minPowerConsumption;
    private final int highestElectricityPriceHour;
    private final int lowestElectricityPriceHour;
    
    
    public ElectricitySummary(float consumedElectricity, float electricityCost,
            String electricityPriceUnit, int averagePowerConsumption, 
            int maxPowerConsumption, int minPowerConsumption,
            int highestElectricityPriceHour, int lowestElectricityPriceHour)
    {
        this.consumedElectricity = consumedElectricity;
        this.electricityCost = Math.round(electricityCost * 100) / 100.0d;
        this.averagePowerConsumption = averagePowerConsumption;
        this.maxPowerConsumption = maxPowerConsumption;
        this.minPowerConsumption = minPowerConsumption;
        this.highestElectricityPriceHour = highestElectricityPriceHour;
        this.lowestElectricityPriceHour = lowestElectricityPriceHour;
        this.electricityPriceUnit = electricityPriceUnit;
    }
}
