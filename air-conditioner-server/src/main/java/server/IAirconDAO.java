package server;

import java.util.List;

interface IAirconDAO
{
    public List<Aircon> getAllAircons();
    public Aircon getAirconById(String id);
    public void updateAircon(Aircon aircon);
    public TemperatureSummary getTemperatureSummary(String id);
    public ElectricitySummary getElectricitySummary(String id);
    public HighestConsumption getHighestPowerConsumptionAircon();
}
