import java.io.*;
import java.net.Socket;

/**
    A class that starts the request/response in a new thread
 */

 
public class HTTPConnection extends Thread
{
    private Socket clientSocket;
    private String recourceRootFolder;
    
    public HTTPConnection(Socket clientSocket, String recourseRootFolder)
    {
        this.clientSocket = clientSocket;
        this.recourceRootFolder = recourseRootFolder;
        start();
    }
    
    public void run()
    {
        System.out.println("new thread: " + currentThread().getName() +" created to: " + clientSocket.getInetAddress().getHostAddress());
        try
        {
            InputStream inStream  = clientSocket.getInputStream();
            OutputStream outStream = clientSocket.getOutputStream();
        
            StringBuilder receivedString = new StringBuilder();
        
            byte[] buffer = new byte[1024];  // TODO, Low server buffer
    
            
                /* make sure server wait until something actually arrive before trying to echo */
            while(inStream.available() < 1)
            {
            
            }
            /* Read the input from client */
           
    
               int readChar = 0;
    
               if(inStream.available() > 0)
               {
                   readChar = inStream.read(buffer);
                   receivedString.append(new String(buffer, 0, readChar));
               }
    
               Response response = new Response(receivedString.toString(), recourceRootFolder);
                /* Input complete
            
                String content = "<html>\n" +
                        "<head>\n" +
                        "<title>...\n" +
                        "\n" +
                        "...lots of HTML code here...\n" +
                        "\n" +
                        "</body></html>\n"
                        + "\r\n";
               
               /* String response = "HTTP/1.1 200 OK\n" +
                        "Content-Length: 132\n" +
                        "\r\n" +
                        "<html>\n" +
                        "<head>\n" +
                        "  <title>An Example Page</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "  Hello World, this is a very simple HTML document.\n" +
                        "</body>\n" +
                        "</html>\n"
                        + "\r\n";
                */
           
           
                /* WORKING HTTP 200 OK, HTML RESPONSE */
            
                String responseString = "HTTP/1.1 200 OK\n" +
                    
                        "Content-Type: text/html; charset=UTF-8\n" +
                        "Content-Encoding: UTF-8\n" +
                        "Content-Length: 131\n" +
                        "Date: Mon, 23 May 2005 22:38:34 GMT\n" +
                        "Last-Modified: Wed, 08 Jan 2003 23:11:55 GMT\n" +    // Apache/1.3.3.7 (Unix) (Red-Hat/Linux)
                        "Server: java run on mac\n" +
                        "Accept-Ranges: bytes\n" +
                        "Connection: close\n" +
                        "\r\n" +
                        "<html>\n" +
                        "<head>\n" +
                        "  <title>An Example Page</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "  Hello World, this is a very simple HTML document.\n" +
                        "</body>\n" +
                        "</html>"
                        + "\r\n";
            
            
            
                buffer = response.getResponseHeader().getBytes();
                outStream.write(buffer);
                
                if(!response.getResponseIncludesBody())
                {
                    InputStream inputStream = new FileInputStream(response.getBodyasFile());
    
                    int readBytes = 0;
    
                    while ((readBytes = inputStream.read(buffer, 0, buffer.length)) > 0) {
                        outStream.write(buffer, 0, readBytes);
                        outStream.flush();
                    }
                    
                    
                   String finalBytes = "/r/n";
                   outStream.write(finalBytes.getBytes());
                   outStream.flush();
                   outStream.close();
                   inputStream.close();
                   
                }
               
                System.out.println("out " + new String(buffer));   // TODO, debug
                
               
                
            
            // TODO test
            // System.out.println(new String(buffer));
        }
        catch(IOException e)
        {
            System.out.println("Exception caught when trying to listen on port "
                    + clientSocket.getLocalPort() + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    
        try
        {
            System.out.println("Closing: " + clientSocket.getInetAddress().getHostAddress() + " on thread: " +currentThread().getName());
            clientSocket.close();
        
        } catch(IOException e)
        {
            System.out.println("Socket couldn't be closed: " + e.getMessage());
        }
    }
}
