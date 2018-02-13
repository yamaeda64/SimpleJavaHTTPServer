import java.io.File;

/**
 * This class will hold a http resonse header and body and will take care of sending those as a stream of data
 */
public class Response
{
  private ResponseHeader header;
  private File body;
  
  public Response(String request, String resourceRootFolder)
  {
      
     RequestParser parser = new RequestParser(request);
      
       body = new File(resourceRootFolder+parser.getPath());
       
       
       
       /* todo need to make sure the server files outside specified folder is safe
          
       if(filePath.length() < soucePath)
          header = new Response403Forbidden(file);
         
          */
       
       if(!body.exists())
          header = new Response404NotFound(body);
       
      
       else
       {
          header = new Response200OK(body);
       }
     
       String response = header.getResponseHeader();
       System.out.println("Response: "+ response);  // TODO, debug
  }
  
  public String getResponse()
  {
      return header.getResponseHeader();
  }
}
