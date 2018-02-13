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
    public String currentTimeField;
    
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
        return "header: ahahsh\n";
    }
}
