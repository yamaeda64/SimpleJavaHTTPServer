package request;
/**
 * Extract the information needed from the HTTP Request
 */
public class RequestParser
{
    private RequestType type;
    private String path;
    private String httpVersion;
    private String boundary;
    
    private long contentLength = -1;
    
    /**
     * Constructor that takes the response as a String
     * @param inputRequest the HTTP Response as a String
     */
    public RequestParser(String inputRequest)
    {
    
        String[] request = inputRequest.split("\n");
    
        String[] firstLine = request[0].split(" ");
        try {
        	type = RequestType.valueOf(firstLine[0]);
        } catch (IllegalArgumentException e) {
        	type = RequestType.ILLEGAL;
        }
        
        if(firstLine.length == 3)
        {
            path = firstLine[1];
            httpVersion = firstLine[2];
            if(!path.startsWith("/"))
            {
                throw new IllegalArgumentException("The path must start with \"/\"");
            }
        }
        
        else
        {
            throw new IllegalArgumentException("The request was in illegal form");
        }
        
        /* Collect the boundary and Content-Length from the request */
        for(String s : request)
        {
            if(s.contains("boundary"))
            {
                int startPos = s.lastIndexOf('=')+1;
                boundary = s.substring(startPos);
            }
            if(s.contains("Content-Length"))
            {
                int startPos = s.lastIndexOf(':')+2;
                contentLength = Integer.parseInt(s.substring(startPos).trim());
            }
        }
        
        /* Adds index.html to the path if the path is a folder and a GET request */
        if(!isPathAFile(path) && type == RequestType.GET)
        {
          if(path.charAt(path.length()-1) == '/')
          {
             path+= "index.html";
          }
          else
          {
             path+="/index.html";
          }
       }
    }
    
    /**
     * Checks if a path has a . after the last slash and then sees the path as a
     * direct path to a file, otherwise the path is concidered a folder.
     * @param path String of the path
     * @return true if path is a file, othervise false
     */
    public boolean isPathAFile(String path)
    {
        int lastSlash = path.lastIndexOf('/');
        
        if(path.substring(lastSlash).contains("."))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    
    /* Getters & Setters */
    public RequestType getType(){return type;}
    public String getPath(){return path;}
    public String getHttpVersion(){return httpVersion;}
    public String getBoundary()
    {
            return boundary;
    }
    public long getContentLength()
    {
        return contentLength;
    }
}
