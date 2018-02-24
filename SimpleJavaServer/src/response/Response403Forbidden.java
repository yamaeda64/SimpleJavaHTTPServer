package response;

public class Response403Forbidden extends ResponseHeader
{
    String responseBody = "<HTML><HEAD><TITLE>403 Forbidden</TITLE></HEAD>" +
            "<BODY><H1>403 Forbidden</H1></BODY></HTML>";
    
    
    protected final String responseStatus = "HTTP/1.1 403 Forbidden\n";
    
    public Response403Forbidden()
    {
        super();
    }
    public String getResponseHeader()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(responseStatus);
        stringBuilder.append("Content-Length:" + responseBody.length());
        stringBuilder.append("\n");
        stringBuilder.append("Content-Type: text/html; charset=UTF-8");
        stringBuilder.append("\n");
        stringBuilder.append("Content-Encoding: UTF-8");
        stringBuilder.append("\n\r\n");
        stringBuilder.append(responseBody);
        
        
        super.setIncludesBody(true);
        return stringBuilder.toString();
    }
}
