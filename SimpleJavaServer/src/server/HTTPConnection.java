package server;

import request.RequestType;
import response.Response;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

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
       // DEBUG
        System.out.println("Start-Active ThreadCount " + Thread.activeCount());
        try
        {
            
            //InputStream inStream  = clientSocket.getInputStream();
            Scanner inStream = new Scanner(clientSocket.getInputStream());
            OutputStream outStream = clientSocket.getOutputStream();
        
            StringBuilder receivedString = new StringBuilder();
            System.out.println("1");
            byte[] buffer;
    
            
                /* make sure server wait until something actually arrive before trying to echo */
          //  while(inStream.available() < 1)
        //    {
         //       System.out.println("2");
        //    }
            
            /* Read the input from client */
            int readChar = 0;
            System.out.println("3");
          
               while(true)
               {
                   String temp = inStream.nextLine();
                   if(temp.isEmpty())
                   {
                       break;
                   }
                   System.out.println("6"); // TODO debug line
                   receivedString.append(temp + "\n");
                   System.out.print(temp);
               }
            System.out.println("7"); // TODO, debug line
               System.out.println(receivedString.toString());
               Response response = null;
               if(receivedString.length()>0)
               {
                   try
                   {
                       response = new Response(receivedString.toString(), recourceRootFolder);
                   }
                   catch(InternalError e)
                   {
                       response = new Response(500);
                   }
                   catch(Exception e)
                   {
                       response = new Response(500);
                   }
               }
               
               //TODO, debug
               System.out.println(receivedString.toString());
            System.out.println("8");
               
            //TODO debug
            
            if(response.getRequestType() == RequestType.POST || response.getRequestType() == RequestType.PUT)
            {
                while(true)
                {
                    String temp = inStream.nextLine();
                    if(temp.isEmpty())
                    {
                        break;
                    }
                    
                    System.out.print("Second reading: " + temp);
                }
                    //String temp = inStream.nextLine();
                   // System.out.println("Second reading: " + temp);
            }
            System.out.println("9");
            /*
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
            
            /*
               EXAMPLE RESPONSE
               
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
            */
            
            if(response != null)
            {
                buffer = response.getResponseHeader().getBytes();
                outStream.write(buffer);
    
                if(!response.includesBody())
                {
                    InputStream inputStream = new FileInputStream(response.getBodyasFile());
    
                    int readBytes = 0;
    
                    while((readBytes = inputStream.read(buffer, 0, buffer.length)) > 0)
                    {
                        outStream.write(buffer, 0, readBytes);
                        //outStream.flush();
                        System.out.println("readBytes: " + readBytes);
                    }
    
    
                    String finalBytes = "/r/n";
                    outStream.write(finalBytes.getBytes());
                    outStream.flush();
                    outStream.close();
                    inputStream.close();
    
                    //TODO, debug
                    System.out.println("Debug -here");
                }
    
                // System.out.println("out " + new String(buffer));   // TODO, debug
    
    
            }
            
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
    
        // DEBUG
        System.out.println("End-Active ThreadCount " + Thread.activeCount());
        
      
    }
  
}
