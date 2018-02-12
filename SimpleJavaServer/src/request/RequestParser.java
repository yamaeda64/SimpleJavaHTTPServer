/**
 * Extract the information needed from the HTTP Request
 */
public class RequestParser
{
    
    private String type;
    private String path;
    private String httpVersion;
    
    /**
     * Constructor that takes the response as a String
     * @param inputRequest the HTTP Response as a String
     */
    public RequestParser(String inputRequest)
    {
        
        String[] request = inputRequest.split("\n");
        
        String[] firstLine = request[0].split(" ");
    
        type = firstLine[0];
        if(firstLine.length == 3)
        {
            path = firstLine[1];
            httpVersion = firstLine[2];
        }
        else if(firstLine.length == 2)
        {
            path = "/";
            httpVersion = firstLine[1];
        }
        else
        {
            throw new IllegalArgumentException("The request was in illegal form");
        }
    }
    
    
    
    /* Getters  */
    public String getType(){return type;}
    public String getPath(){return path;}
    public String getHttpVersion(){return httpVersion;}
}
