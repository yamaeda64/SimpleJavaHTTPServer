package response;

import redirect.RedirectList;
import request.RequestParser;
import request.RequestType;

import java.io.File;
import java.io.IOException;

/**
 * This class will hold a http resonse header and body and will take care of sending those as a stream of data
 */
public class Response
{
  private ResponseHeader header;
  protected File body;
  private RequestParser parser;

  public Response(String request, String resourceRootFolder)
  {
     
      try
      {
          parser = new RequestParser(request);
          body = new File(getCorrectPath(resourceRootFolder, parser.getPath()));
          RedirectList redirectList = new RedirectList();
          
          if(isOutsideSourceFolder(resourceRootFolder))
          {
              header = new Response403Forbidden(body);
          } else if(!isValidRequestType())
          {
              header = new Response400BadRequest(body);
          } else if(redirectList.isMoved(parser.getPath()))
          {
              header = new Response302Found(body, redirectList.getRedirectPath(parser.getPath()));
          } else if(!body.exists())
          {
              header = new Response404NotFound(body);
          } else
          {
              header = new Response200OK(body);
          }
      }
      catch(IllegalArgumentException e)
      {
         parser = new RequestParser("ILLEGAL / HTTP/1.1");
          header =new Response400BadRequest();
      }

     
      
	  String response = header.getResponseHeader();
	  System.out.println("Response: "+ response);  // TODO, debug
  }
    
    public Response(int statusCode)
    {
        if(statusCode == 500)
        {
            parser = new RequestParser("ILLEGAL / HTTP/1.1");
            body = null;
            header = new Response500InternalServerError();
        }
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
    

    private boolean isValidRequestType()
    {
    	if(getRequestType() == RequestType.ILLEGAL) {
    		return false;
    	}
    	return true;
    }
    
    private boolean isTempMoved() {
    	if(parser.getPath().equals("/htm.htm")) {
    		return true;
    	}
    	return false;
    }

    public RequestType getRequestType()
    {
        return parser.getType();
    }
}
