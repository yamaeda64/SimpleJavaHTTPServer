package server;

import request.RequestType;
import response.Response;

import java.io.*;
import java.net.Socket;

/**
 A class that starts the request/response in a new thread
 */

public class HTTPConnection extends Thread
{
    private Socket clientSocket;
    private String resourceRootFolder;
    
    public HTTPConnection(Socket clientSocket, String recourseRootFolder)
    {
        this.clientSocket = clientSocket;
        this.resourceRootFolder = recourseRootFolder;
        start();
    }
    
    public void run()
    {
        System.out.println("new thread: " + currentThread().getName() + " created to: " + clientSocket.getInetAddress().getHostAddress());
    
        try
        {
             /* Initialization */
            InputStream inStream = clientSocket.getInputStream();
            OutputStream outStream = clientSocket.getOutputStream();
        
            /* recieve request as string */
            String inputString = recieveRequestHeader(inStream);
            
            /* Sending the received header to Response class for parsing */
            Response response = null;
            if(inputString.length() > 0)
            {
                try
                {
                    response = new Response(inputString, resourceRootFolder);
                } catch(InternalError e)
                {
                    response = new Response(500, resourceRootFolder); // Response 500 if internal error happened
                } catch(Exception e)
                {
                    response = new Response(500, resourceRootFolder);
                }
            }
            
          /* If request is POST or PULL, read body */
            int responseNumber = 500;
            if(inputString.length() > 5 && (response.getRequestType() == RequestType.POST || response.getRequestType() == RequestType.PUT))
            {
                try
                {
                    responseNumber = recieveFileToServer(inStream, response);
                } catch(IllegalArgumentException e)
                {
                    responseNumber = 400;   // File name is not supported
                } catch(Exception e)
                {
                    responseNumber = 500;   // Unknown error
                }
    
                response = new Response(responseNumber, resourceRootFolder + "/index.html");
            }
    
    
            byte[] buffer;
    
    
            if(inputString.length() > 5)
            {
                buffer = response.getResponseHeader().getBytes();
    
                outStream.write(buffer);
    
                if(!response.includesBody())
                {
                    InputStream inputStream = new FileInputStream(response.getBodyasFile());
    
                    int readBytes = 0;
    
                    while((readBytes = inputStream.read(buffer, 0, buffer.length)) != -1)
                    {
                        outStream.write(buffer, 0, readBytes);
                    }
    
                    String finalBytes = "/r/n";
                    outStream.write(finalBytes.getBytes());
                    outStream.flush();
                    outStream.close();
                    inputStream.close();
    
                }
    
            }
    
        } catch(IOException e)
        {
            System.out.println("Exception caught when trying to listen on port "
                    + clientSocket.getLocalPort() + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    
        try
        {
            System.out.println("Closing: " + clientSocket.getInetAddress().getHostAddress() + " on thread: " + currentThread().getName());
            clientSocket.close();
    
        } catch(IOException e)
        {
            System.out.println("Socket couldn't be closed: " + e.getMessage());
        }
    }
    
    private String recieveRequestHeader(InputStream inputStream) throws IOException
    {
        StringBuilder stringBuilder = new StringBuilder();
    
        boolean isLooping = true;
        while(isLooping)
        {
            int receivedInt = inputStream.read();
            if(receivedInt == -1)
            {
                isLooping = false;
                break;
            }
            char temp = (char)receivedInt;
            stringBuilder.append(temp);
            if(temp == '\r')
            {
                temp = (char) inputStream.read();
                stringBuilder.append(temp);
                if(temp == '\n')
                {
                    temp = (char) inputStream.read();
                    stringBuilder.append(temp);
                    if(temp == '\r')
                    {
                        isLooping = false;
                    }
                }
            }
        }
        return stringBuilder.toString();
    }
    
    /**
     * Takes care of recieving the body of a put or post request and write the file to the specific path of the request
     * Limited to multipart/form-data with single file á request
     * @param inputStream the stream that reads the data from HTTP client
     * @param response The response recieved as header of the Post/Put request
     * @return returns the appropriate response status code
     * @throws IOException
     */
    private int recieveFileToServer(InputStream inputStream, Response response) throws IOException
    {
        int byteCounter = 0;
        StringBuilder preBody = new StringBuilder();
    
        boolean isLooping = true;
    
        /* Read the head of the transmission data */
        while(isLooping)
        {
            char temp = (char) inputStream.read();
            byteCounter++;
            preBody.append(temp);
            if(temp == '\r') //  temp == '\n' ||
            {
                temp = (char) inputStream.read();
                byteCounter++;
                preBody.append(temp);
                if(temp == '\n')
                {
                    temp = (char) inputStream.read();
                    byteCounter++;
                    preBody.append(temp);
                    if(temp == '\r')
                    {
                        byteCounter++;
                        isLooping = false;
                        inputStream.read();  // consume last newLine
                    }
                }
            }
        }
                
        /* Extract the important details from the transmision head */
        String contentDisposition = "";
        String fileName = "";
        String formName = "";
      
        for(String s : preBody.toString().split("\n"))
        {
            if(s.startsWith("Content-Disposition"))
            {
                String[] temp = s.split(";");
                for(String string : temp)
                {
                    if(string.startsWith("Content-Disposition: "))
                    {
                        int contentStart = string.indexOf(':')+2;
                        contentDisposition = string.substring(contentStart);
                    }
                    else if(string.startsWith(" name"))
                    {
                        int contentStart = string.indexOf('=')+2;
                        formName = string.substring(contentStart,string.length()-1);
                    }
                    else if(string.startsWith(" filename"))
                    {
                        int contentStart = string.indexOf('=') + 2;
                        int contentEnd = string.lastIndexOf('"');
                        if(contentEnd < contentStart)
                        {
                            throw new IllegalArgumentException("Filename not supported");
                        }
                        fileName = string.substring(contentStart, contentEnd);
                    }
                }
            }
        }
                
        /* Make the filepath */
        String path = resourceRootFolder+response.getPath();
        int lastSlash = path.lastIndexOf('/') ;
        path = path.substring(0,lastSlash+1);
    
        File pathChecker = new File(path);
        if(!pathChecker.isDirectory())
        {
            return 404;
        }
        String outputPath = path + fileName;
               
        /* If POST, it should make unique file for each upload, if PUT it will just overwrite a file uploaded with same name */
        File outputFile = new File(outputPath);
        if(response.getRequestType()==RequestType.POST)
        {
            if(outputFile.exists())
            {
                boolean fileIsDuplicate = true;
                int counter = 1;
                while(fileIsDuplicate)
                {
                    int extensionPos = outputPath.indexOf('.');
                    if(outputPath.matches(".+_[0-9]+\\..+"))
                    {
                        int startNumber = outputPath.lastIndexOf('_')+1;
                        int endNumber = outputPath.lastIndexOf('.');
                        counter = Integer.parseInt(outputPath.substring(startNumber,endNumber));
                        counter++;
                    
                        outputPath = outputPath.substring(0,startNumber) + counter + outputPath.substring(endNumber);
                    
                        outputFile = new File(outputPath);
                        if(!outputFile.exists())
                        {
                            fileIsDuplicate = false;
                        }
                    }
                    else
                    {
                        int endNumber = outputPath.lastIndexOf('.');
                        outputPath = outputPath.substring(0,endNumber) + "_" +counter + outputPath.substring(endNumber);
                        outputFile = new File(outputPath);
                        if(!outputFile.exists())
                        {
                            fileIsDuplicate = false;
                        }
                    }
                }
            }
        }
        FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
        isLooping=true;
        int pfCounter = 0;
        String checkString = "--" + response.getBoundary() + "--";
        byte[] tempWhileEndChecking = new byte[checkString.length()+1];
        int charCounter = 0;
        int readBytes = 0;
        long responseLength = response.getContentLength();
    
        byte[] postedFileBuffer = new byte[1024*8];
    
        while(byteCounter < responseLength -100)
        {
            int bytesToRead = (int)(responseLength - byteCounter -100);
            {
                if(bytesToRead > postedFileBuffer.length)
                {
                    bytesToRead = postedFileBuffer.length-1;
                }
            }
        
            readBytes = inputStream.read(postedFileBuffer,0,bytesToRead);
            byteCounter += readBytes;
            fileOutputStream.write(postedFileBuffer,0,readBytes);
        }
       
        /* Starting checking for end of file */
        while(isLooping)
        {
            charCounter++;
            byte temp = (byte)inputStream.read();
            byteCounter++;
                    
            /* Start to check if end of file after each line */
            if(temp == '\n')
            {
                /* Output the last sentence to file and reset buffer */
                fileOutputStream.write(postedFileBuffer,0, pfCounter);
                pfCounter = 0;
                        
                /* save temp to a new array and save it if not matching end of stream */
                tempWhileEndChecking[0] = temp;
                boolean matchesEndChecking = true;
                int endCheckingCounter = 1;
            
                while(endCheckingCounter < checkString.length()-2 && matchesEndChecking)
                {
                    tempWhileEndChecking[endCheckingCounter] = (byte)inputStream.read();
                    byteCounter++;
                
                    if((char)tempWhileEndChecking[endCheckingCounter] == checkString.charAt(endCheckingCounter-1))
                    {
                        endCheckingCounter++;
                    }
                    else
                    {
                        matchesEndChecking = false;
                                
                        /* If wasn't end of stream, put the temp array to main buffer */
                        for(int i= 0; i<=endCheckingCounter; i++)
                        {
                            postedFileBuffer[pfCounter++] = tempWhileEndChecking[i];
                        }
                    }
                }
                
                if(matchesEndChecking == true)
                {
                    isLooping = false;
                }
            
            }
            /* if wasn't end condition, place saved bytes to the buffer */
            else
            {
                postedFileBuffer[pfCounter++] = temp;
            }
        }
        fileOutputStream.close();
        
        return 201;
    }
    
}
