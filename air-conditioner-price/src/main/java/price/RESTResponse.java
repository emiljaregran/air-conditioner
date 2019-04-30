package price;

import java.util.ArrayList;
import java.util.List;

public class RESTResponse 
{
    int code;
    String message;
    List<String> response = new ArrayList<>();
    
    public void setCode(int code)
    {
        this.code = code;
    }
    
    public void setMessage(String message)
    {
        this.message = message;
    }
    
    public String getResponse()
    {
        return "Code: " + code + " Message: " + message;
    }
}
