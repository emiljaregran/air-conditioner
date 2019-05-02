package rest_client;

public class ResponseBody
{
    private final int code;
    private final String message;
    
    public ResponseBody(int code, String message)
    {
        this.code = code;
        this.message = message;
    }
}
