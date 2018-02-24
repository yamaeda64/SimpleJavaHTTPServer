package response;


public class Response201Created extends ResponseHeader
{
    String redirectPath;
    
    public Response201Created(String redirectPath)
    {
        super();
        this.redirectPath = redirectPath;
    }
    
    protected final String responseStatus = "HTTP/1.1 201 Created\n";
    
    public String getResponseHeader()
    {
        String responseBody2 = "<HTML><HEAD><TITLE>201 Created</TITLE></HEAD>" +
                "<BODY><H1>201 Created</H1></BODY></HTML>";
        
        String responseBody = "";
        
        
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(responseStatus);
        stringBuilder.append("Content-Length:" + responseBody.length());
        stringBuilder.append("\n");
        stringBuilder.append("Resource Location: "+ redirectPath);
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
