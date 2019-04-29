package server;

import java.util.List;

interface IAirconDAO
{
    public List<Aircon> getAllAircons();
    public Aircon getAirconById(String id);
    public void updateAircon(Aircon aircon);
}
