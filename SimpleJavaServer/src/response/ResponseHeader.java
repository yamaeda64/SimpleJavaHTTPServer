import java.io.File;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * This class will hold the header for the response
 */
public class ResponseHeader
{
    private long contentLength;
    private LocalDateTime currentDateTime;
    private String contentEncoding;                 // TODO, read up on content encoding
    private LocalDateTime sourceLastModified;
    private String contentType;                     // TODO, manage to write this from the file extension
    private String currentTimeField;
    private boolean includesBody;             // A boolean to set if response has a message included, if false body should be attatched
    
    public ResponseHeader(File file)
    {
        this.contentLength = file.length();
        
        // TODO, check if times is correct and fix them to GMT
        Date lastModifiedTemp = new Date(file.lastModified());
        sourceLastModified = lastModifiedTemp.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        
        currentDateTime = LocalDateTime.now(Clock.systemUTC());
        currentTimeField = "Date: " + currentDateTime.toString();  // todo reformat
    }
    
    public String getResponseHeader()
    {
        return "";
    }
    
    public String getCurrentTimeField()
    {
        return currentTimeField;
    }
    
    public String getLastModifiedField()
    {
        return "Last-Modified: " + sourceLastModified.toString();
    }
    
    public void setIncludesBody(boolean includesBody)
    {
        this.includesBody = includesBody;
    }
    
    public boolean getIncludesBody()
    {
        return includesBody;
    }
}
