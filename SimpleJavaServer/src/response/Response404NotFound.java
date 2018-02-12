import java.io.File;

/**
 * Created by joakimbergqvist on 2018-02-12.
 */
public class Response404NotFound extends ResponseHeader
{
    public Response404NotFound(File file)
    {
        super(file);
    }
    protected final String responseStatus = "HTTP/1.1 404 Not Found";
}
