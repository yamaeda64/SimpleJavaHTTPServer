import java.io.File;


public class Response500InternalServerError extends ResponseHeader
{
    public Response500InternalServerError(File file)
    {
        super(file);
    }
    protected final String responseStatus = "HTTP/1.1 500 Internal Server Error\n";
    
    public String getResponseHeader()
    {
        String responseBody = "<HTML><HEAD><TITLE>500 Internal Server Error</TITLE></HEAD>" +
                                    "<BODY><H1>500 Internal Server Error</H1></BODY></HTML>";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(responseStatus);
        stringBuilder.append("Content-Length:" + responseBody.length());
        stringBuilder.append("\n");
        stringBuilder.append("Content-Type: text/html; charset=UTF-8");
        stringBuilder.append("\n");
        stringBuilder.append("Content-Encoding: UTF-8");
        stringBuilder.append("\n");
        stringBuilder.append(getCurrentTimeField());
        stringBuilder.append("\n");
        stringBuilder.append("\r\n");
        stringBuilder.append(responseBody);
        stringBuilder.append("\n");
        stringBuilder.append("\r\n");
       
        super.setIncludesBody(true);
        
        return stringBuilder.toString();
    }
}
