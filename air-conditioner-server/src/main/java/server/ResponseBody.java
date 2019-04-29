package server;

public class ResponseBody
{
    int code;
    String message;
    
    public ResponseBody(int code, String message)
    {
        this.code = code;
        this.message = message;
    }
}
