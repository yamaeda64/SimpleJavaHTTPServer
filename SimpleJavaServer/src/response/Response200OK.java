package response;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * This Class is extending the ResponseHeader and will be used for a 200 OK response
 */
public class Response200OK extends ResponseHeader
{
    private File file;
    
    public Response200OK()
    {
        super();
    }
    public Response200OK(File file)
    {
        super(file);
        this.file = file;
    }
    protected final String responseStatus = "HTTP/1.1 200 OK\n";
    String responseBody = "<HTML><HEAD><TITLE>200 OK</TITLE></HEAD>" +
            "<BODY><H1>200 OK</H1></BODY></HTML>";
    
    public String getResponseHeader()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(responseStatus);
        if(file != null)
        {
            
            stringBuilder.append("Content-Length:" + file.length());
            stringBuilder.append("\n");
            try {
                stringBuilder.append("Content-type: " + Files.probeContentType(file.toPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            stringBuilder.append("\n");
        }
        
        if(file == null)
        {
            super.setIncludesBody(true);
        }
        else
        {
            stringBuilder.append(getLastModifiedField());
            super.setIncludesBody(false);
        }
        
        stringBuilder.append("\n");
        stringBuilder.append(getCurrentTimeField());
        stringBuilder.append("\n");
        stringBuilder.append("Connection: close\n");
        stringBuilder.append("Cache-Control: no-store\n");
        stringBuilder.append("\r\n");
        
        if(super.includesBody() == true)
        {
            stringBuilder.append(responseBody);
        }
        
        return stringBuilder.toString();
    }
}
