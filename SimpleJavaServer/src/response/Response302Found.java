package response;

import java.io.File;

public class Response302Found extends ResponseHeader
{
    String responseBody = "<HTML><HEAD><TITLE>302 Found</TITLE></HEAD>" +
            "<BODY><H1>302 Found</H1></BODY></HTML>";
    String redirectPath;
    
    public Response302Found(File file, String redirectPath)
    {
        super(file);
        this.redirectPath = redirectPath;
    }
    protected final String responseStatus = "HTTP/1.1 302 Found\n";
    
    public String getResponseHeader()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(responseStatus);
        stringBuilder.append("Location:" + redirectPath);
        stringBuilder.append("\n\r\n");
        stringBuilder.append(responseBody);
        
        
        
        super.setIncludesBody(true);
        return stringBuilder.toString();
    }
}
