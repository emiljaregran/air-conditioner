package server;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
@XmlRootElement(name = "aircon")


public class Aircon implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String id;

    public Aircon() {}
    
    public Aircon(String id)
    {
        this.id = id;
    }
    
    @SerializedName("w")
    public String getId()
    {
        return id;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
}
