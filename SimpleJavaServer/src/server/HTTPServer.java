import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * The main class of a simpla HTTP Server.
 */

public class HTTPServer
{
    /**
     * The main method
     * @param args args[0] = port (a port number between 1 - 65535, administrator access is needed for port 1-1023
     * @param args args[1] = resource root folder
     * @throws IOException
     */
    public static void main(String[] args) throws IOException
    {
        int port = 0;
        String resourceRootFolder;
        
        if(args.length != 2)
        {
            System.out.println("Wrong number of arguments, should be 2. \n" +
                    "first: port number, second: root folder");
            System.exit(1);
        }
        
        try
        {
            port = Integer.parseInt(args[0]);
        }
        catch(Exception e)
        {
            System.out.println("The port wasn't all numerical");
            System.exit(1);
        }
        if(port<=0 || port > 0xFFFF)
        {
            System.out.println("The port wasn't a correct port number");
            System.exit(1);
        }
        if(args[1].charAt(args[1].length()-1) == '/')
        {
            resourceRootFolder = args[1].substring(0,args[1].length()-2);   // removes final / in path
        }
        else
        {
            resourceRootFolder = args[1];
        }
        try
        {
            File file = new File(resourceRootFolder);
            
            if(!file.isDirectory())
            {
                System.out.println("The resource folder wasn't an directory");
                System.exit(1);
            }
        }
        catch(Exception e)
        {
            System.out.println("there was a problem with the resource folder");
            System.exit(1);
        }
        
        try
        {
            ServerSocket serverSocket = new ServerSocket(port);
            
            System.out.println("server start listening... ... ...");
            
            int counter = 0;
            /* Listen if anyone connects to the clientSocket, if so, start a new thread */
            while(true)
            {
                // TODO DEBUG  (remove counter above)
                counter++;
              
                    System.out.println("Main ThreadCount: " + Thread.activeCount());
               
                //END DEBUG
                Socket clientSocket;
                clientSocket = serverSocket.accept();
                HTTPConnection theConnection = new HTTPConnection(clientSocket, resourceRootFolder);
            }
            
        }
        catch(IOException e)
        {
            System.out.println("There was a problem setting up the sockets.");
        }
    }

}
