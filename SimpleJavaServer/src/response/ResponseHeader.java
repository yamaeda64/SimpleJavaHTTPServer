package response;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * This class will hold the header for the response
 */
public class ResponseHeader
{
    private long contentLength;
    private String contentEncoding;
    private LocalDateTime sourceLastModified;
    private String contentType;
    private String currentTimeField;
    private boolean includesBody;             // A boolean to set if response has a message included, if false body should be attatched
    
    
    public ResponseHeader()
    {
        contentLength = 0;
        currentTimeField = "Date: " + DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneId.of("GMT")));
    }
    
    public ResponseHeader(File file)
    {
        this.contentLength = file.length();
        
        /* Converts last modified from current Timezone to GMT */
        Date lastModifiedTemp = new Date(file.lastModified());  // get time in millis from Epoch
        sourceLastModified = lastModifiedTemp.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        currentTimeField = "Date: " + DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneId.of("GMT")));
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
        return "Last-Modified: " +
                DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime
                        .of(sourceLastModified, ZoneId.systemDefault())
                                .withZoneSameInstant(ZoneId.of("GMT")));
    }
    
    public void setIncludesBody(boolean includesBody)
    {
        this.includesBody = includesBody;
    }
    
    public boolean includesBody()
    {
        return includesBody;
    }
}
