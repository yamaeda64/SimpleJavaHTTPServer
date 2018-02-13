import java.io.File;

/**
 * This Class is extending the ResponseHeader and will be used for a 200 OK response
 */
public class Response200OK extends ResponseHeader
{
    public Response200OK(File file)
    {
        super(file);
    }
    protected final String responseStatus = "HTTP/1.1 200 OK\n";
    
    public String getResponseHeader()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(responseStatus);
        stringBuilder.append(super.getResponseHeader());
        return stringBuilder.toString();
    }
    
}
