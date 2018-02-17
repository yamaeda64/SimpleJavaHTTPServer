package response;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * This Class is extending the ResponseHeader and will be used for a 200 OK response
 */
public class Response200OK extends ResponseHeader
{
    File file;
    public Response200OK(File file)
    {
        super(file);
        this.file = file;
    }
    protected final String responseStatus = "HTTP/1.1 200 OK\n";
    
    public String getResponseHeader()
    {
        
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(responseStatus);
        stringBuilder.append("Content-Length:" + file.length());
        stringBuilder.append("\n");
        
        // TODO, add charset? + double check if there's an issue using the probeContentType approach
        try {
            stringBuilder.append("Content-type: " + Files.probeContentType(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stringBuilder.append("\n");
        
        //stringBuilder.append("Content-Type: text/html; charset=UTF-8");   // TODO, this must differ depending on filetype
        //stringBuilder.append("\n");
        //stringBuilder.append("Content-Encoding: UTF-8");                    // TODO, this must differ depending on filetype (encoding)
        //  stringBuilder.append("Content-Type: image/png");
        // stringBuilder.append("\n");
        stringBuilder.append(getLastModifiedField());
        stringBuilder.append("\n");
        stringBuilder.append(getCurrentTimeField());
        stringBuilder.append("\n");
        // TODO test with cach and connection
        stringBuilder.append("Connection: close\n");
        stringBuilder.append("Cache-Control: no-store\n");
        stringBuilder.append("\r\n");
        
        super.setIncludesBody(false);
        
        return stringBuilder.toString();
    }
    
}
