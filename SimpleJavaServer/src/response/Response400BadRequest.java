package response;

public class Response400BadRequest extends ResponseHeader
{
    String responseBody = "<HTML><HEAD><TITLE>400 Bad Request</TITLE></HEAD>" +
            "<BODY><H1>400 Bad Request</H1></BODY></HTML>";
    
    public Response400BadRequest()
    {
        super();
    }
   
    protected final String responseStatus = "HTTP/1.1 400 Bad Request\n";
    
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
