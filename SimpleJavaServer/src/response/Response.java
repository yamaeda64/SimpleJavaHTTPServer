import java.io.File;
import java.io.IOException;

/**
 * This class will hold a http resonse header and body and will take care of sending those as a stream of data
 */
public class Response
{
  private ResponseHeader header;
  protected File body;

  public Response(String request, String resourceRootFolder)
  {
	  RequestParser parser = new RequestParser(request);

	  body = new File(getCorrectPath(resourceRootFolder, parser.getPath()));
      
      if(isOutsideSourceFolder(resourceRootFolder) || !isValidRequestType(parser.getType())) // TODO, should 403 be displayed if it's not a GET request?
      {
          header = new Response403Forbidden(body);
      }
      else if(!body.exists())
      {
          header = new Response404NotFound(body);
      }
	  else
	  {
		  header = new Response200OK(body);
	  }

	  String response = header.getResponseHeader();
	  System.out.println("Response: "+ response);  // TODO, debug
  }
    
    
    public String getResponseHeader()
  {
      return header.getResponseHeader();
  }
  
  public boolean includesBody()
  {
      return header.includesBody();
  }
  public File getBodyasFile()
  {
	  return body;
  }

  /** Checks if the full path leads to a file, otherwise it tries to change the file ending from ".htm" to ".html" 
   * and vice versa to see if it leads to a file and if so, returns the full path to the file
   * @param resourceRootFolder The root source folder
   * @param path Path from root folder
   * @return A string with the correct path. If the original path leads to a file, that is immediately returned. 
   *  Otherwise it checks if path ending with .htm or .html leads to a file and returns that path
   */
  private String getCorrectPath(String resourceRootFolder, String path) 
  {
	  String fullPath = resourceRootFolder + path;

	  // If the full path leads to a file, return it immediately
	  if(new File(fullPath).isFile()) {
		  return fullPath;
	  }

	  String pathEnding = path.split("\\.")[1];

	  // Checks if the file ending is "htm" (and does not lead to a file), then change file ending to "html" and return the path
	  if(pathEnding.equals("htm")) {
		  return fullPath += "l";
	  } else if(pathEnding.equals("html")) { // Returns path ending with "htm" if there's a ".htm" file but no ".html" file
		  String htmPath = fullPath.substring(0, fullPath.length() - 1);
		  if(new File(htmPath).isFile()) {
			  return htmPath;
		  }
	  }

	  // This only runs if there's no file at the full path
	  return fullPath;
  }
    
    /**
     * Checks if the requested folder is outside the source folder to prevent
     * anyone reaching files in the computer that's not in the source folder.
     * @param resourceFolder The root source folder
     * @return boolean value of true if outside and a 403 should be returned, if false it's ok to continue with the request
     */
    private boolean isOutsideSourceFolder(String resourceFolder)
    {
        try
        {
            if(body.getCanonicalPath().startsWith(resourceFolder))
            {
                return false;
            }
            else
            {
                return true;
            }

        }
        catch(IOException e)
        {
            System.out.println("there was an exception when checking security");
        }
        throw new InternalError("There was a problem when reading the requested file");
    }
    
    private boolean isValidRequestType(String type) {
    	String[] validRequests = {"GET"}; // TODO, add "POST" and "PUT" for VG tasks in Problem 2
    	
    	for(String request : validRequests) {
    		if(type.equals(request)) {
        		return true;
        	}
    	}
    	return false;
    }
}
