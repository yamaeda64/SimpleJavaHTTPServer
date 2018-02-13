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
    protected final String responseStatus = "HTTP/1.1 404 Not Found\n";
    
    public String getResponseHeader()
    {
        String responseBody = "<HTML><HEAD><TITLE>404 Not found</TITLE></HEAD>" +
                                    "<BODY><H1>404 Not found</H1></BODY></HTML>";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(responseStatus);
        stringBuilder.append("Content-Length:" + responseBody.length());
        stringBuilder.append("\n");
        stringBuilder.append("Content-Type: text/html; charset=UTF-8");
        stringBuilder.append("\n");
        stringBuilder.append("Content-Encoding: UTF-8");
        stringBuilder.append("\n");
        stringBuilder.append(currentTimeField);
        stringBuilder.append("\n");
        stringBuilder.append("\r\n");
        stringBuilder.append(responseBody);
        stringBuilder.append("\n");
        stringBuilder.append("\r\n");
        return stringBuilder.toString();
    }
}
