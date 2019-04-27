package server;

import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
@Path("/aircons")

public class AirconService
{
    IAirconDAO airconDAO = new AirconDAO();
    
    @GET
    @Path("/")
    @Produces (MediaType.APPLICATION_JSON)
    public List<Aircon> test()
    {
        List<Aircon> airconssss = airconDAO.getAllAircons();
        return airconssss;
    }
}
