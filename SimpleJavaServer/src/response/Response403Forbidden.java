import java.io.File;


public class Response403Forbidden extends ResponseHeader
{
    public Response403Forbidden(File file)
    {
        super(file);
    }
    protected final String responseStatus = "HTTP/1.1 403 Forbidden\n";
    
    public String getResponseHeader()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(responseStatus);
        //stringBuilder.append(super.getResponseHeader());
    
        super.setIncludesBody(true);
        return stringBuilder.toString();
    }
}
