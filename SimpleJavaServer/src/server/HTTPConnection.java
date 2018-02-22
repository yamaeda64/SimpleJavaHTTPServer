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
        System.out.println("new thread: " + currentThread().getName() +" created to: " + clientSocket.getInetAddress().getHostAddress());
        // DEBUG
        System.out.println("Start-Active ThreadCount " + Thread.activeCount());
        try
        {
            
            InputStream inStream = clientSocket.getInputStream();
            OutputStream outStream = clientSocket.getOutputStream();
        
        
            /* Read the header from the input from client */
            
            StringBuilder inputString = new StringBuilder();
            System.out.println("3");
            
            boolean isLooping = true;
            while(isLooping)
            {
                
                char temp = (char) inStream.read();
                inputString.append(temp);
                if(temp == '\r') //  temp == '\n' ||
                {
                    
                    temp = (char) inStream.read();
                    inputString.append(temp);
                    if(temp == '\n')
                    {
                        temp = (char) inStream.read();
                        inputString.append(temp);
                        if(temp == '\r')
                        {
                            System.out.println("BREAKPOINT");
                            System.out.println("nextChar" + (char) inStream.read());
                            isLooping = false;
                        }
                    }
                }
            }
            
            /* Sending the received header to Response class for parsing */
            Response response = null;
            if(inputString.length() > 0)
            {
                try
                {
                    response = new Response(inputString.toString(), resourceRootFolder);
                } catch(InternalError e)
                {
                    response = new Response(500);
                } catch(Exception e)
                {
                    response = new Response(500);
                }
            }
            
            //TODO, debug
            System.out.println(inputString.toString());
            System.out.println("8");
               
          
          /* If request is POST or PULL, read body */
            int byteCounter = 0;
            StringBuilder preBody = new StringBuilder();
            if(response.getRequestType() == RequestType.POST || response.getRequestType() == RequestType.PUT)
            {
                isLooping = true;
    
                /* Read the head of the transmission data */
                while(isLooping)
                {
                    char temp = (char) inStream.read();
                    byteCounter++;
                    preBody.append(temp);
                    if(temp == '\r') //  temp == '\n' ||
                    {
                        temp = (char) inStream.read();
                        byteCounter++;
                        preBody.append(temp);
                        if(temp == '\n')
                        {
                            temp = (char) inStream.read();
                            byteCounter++;
                            preBody.append(temp);
                            if(temp == '\r')
                            {
                                System.out.println("BREAKPOINT");
                                System.out.println("nextChar" + (char) inStream.read());
                                byteCounter++;
                                isLooping = false;
                            }
                        }
                    }
                }
                
                /* Extract the important details from the transmision head */
                String contentDisposition = "";
                String fileName = "";
                String formName = "";
                System.out.println("boundaryFromRequest: " + response.getBoundary());
                System.out.println("preBODY: " + preBody.toString());
                for(String s : preBody.toString().split("\n"))
                {
                    if(s.startsWith("Content-Disposition"))
                    {
                        String[] temp = s.split(";");
                        for(String string : temp)
                        {
                            System.out.println("11111: " +string);
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
                                int contentEnd = string.indexOf('"', contentStart);
                                fileName = string.substring(contentStart, contentEnd);
                            }
                        }
                    }
                }
                
                byte[] postedFileBuffer = new byte[1024*1024];
                String path = resourceRootFolder+response.getPath();
                int lastSlash = path.lastIndexOf('/') ;
                path = path.substring(0,lastSlash+1);
                
                File pathChecker = new File(path);
                if(!pathChecker.isDirectory())
                {
                    pathChecker.mkdir();
                }
                String outputPath = path + fileName;
               
                /* If POST, it should make unique file for each upload, if PUT it will just overwrite a file uploaded with same name */
               File outputFile = new File(outputPath);
               if(response.getRequestType()==RequestType.POST)
                {
                    
                    if(outputFile.exists())
                    {
                        System.out.println("file exists");
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
                System.out.println("outputPath = " + outputPath);
                FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
                isLooping=true;
                int pfCounter = 0;
                String checkString = "--" + response.getBoundary() + "--";
                byte[] tempWhileEndChecking = new byte[checkString.length()+1];
                int charCounter = 0;
                
                while(isLooping)
                {
                    charCounter++;
                    byte temp = (byte)inStream.read();
                    
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
                        
                       
                        while(endCheckingCounter < checkString.length()-2 && matchesEndChecking) // todo, why -2 is needed??
                        {
                            tempWhileEndChecking[endCheckingCounter] = (byte)inStream.read();
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
                        else
                        {
                            
                        }
                        
                        
                    }
                    else
                    {
                        postedFileBuffer[pfCounter++] = temp;
                    }
                    
                }
                
                System.out.println("Final ByteCounter: " + byteCounter);
                
                /* Output the buffered data to file */
                //System.out.println("before write");
              
                
              // Outputs the whole buffer
                /*fileOutputStream.write(postedFileBuffer, 0, pfCounter);
                fileOutputStream.flush();
                fileOutputStream.close();*/
                
                System.out.println("prebody" + preBody);
                String newTemp = "";
            
            }
            System.out.println("9");
            
           
           
            byte[] buffer = new byte[1024];
            
            System.out.println("response " + response==null);
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
