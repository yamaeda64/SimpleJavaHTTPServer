import java.io.File;

/**
 * Created by joakimbergqvist on 2018-02-12.
 */
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
        stringBuilder.append(super.getResponseHeader());
        return stringBuilder.toString();
    }
}
