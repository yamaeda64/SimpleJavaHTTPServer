package tests;

import org.junit.Test;
import request.*;
import static org.junit.Assert.assertEquals;


/**
 * Created by joakimbergqvist on 2018-01-19.
 */
public class RequestParserTest
{
    public final String stringGETRequest = "GET / HTTP/1.1\n" +
            "Host: 127.0.0.1:4567\n" +
            "Connection: keep-alive\n" +
            "Cache-Control: max-age=0\n" +
            "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36\n" +
            "Upgrade-Insecure-Requests: 1\n" +
            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8\n" +
            "Accept-Encoding: gzip, deflate, br\n" +
            "Accept-Language: en-US,en;q=0.9,sv;q=0.8\r\n";
    
    public final String stringGETRequestWithLongerPath = "GET /myFolder/secondFolder/image.png HTTP/1.1\n" +
            "Host: 127.0.0.1:4567\n";
    
    public final String stringPostRequest = "POST / HTTP/1.1\n" +
            "Host: 127.0.0.1:4567\n" +
            "Connection: keep-alive\n" +
            "Cache-Control: max-age=0\n" +
            "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36\n" +
            "Upgrade-Insecure-Requests: 1\n" +
            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8\n" +
            "Accept-Encoding: gzip, deflate, br\n" +
            "Accept-Language: en-US,en;q=0.9,sv;q=0.8\n\r\n" +
            "Name=AnInputName\n\r\n";
    
    public final String stringFAULTRequest = "FAULT / HTTP/1.1\n" +
            "Host: 127.0.0.1:4567\n";
            
    @Test
    public void ParseRequest_TypeShouldBeGET()
    {
        RequestParser parser = new RequestParser(stringGETRequest);
        RequestType actual = parser.getType();
        assertEquals(RequestType.GET , actual);
    }
    
    @Test
    public void ParseRequest_PathShouldBeIndex()
    {
        RequestParser parser = new RequestParser(stringGETRequest);
        String actual = parser.getPath();
        assertEquals("/index.html", actual);
    }
    
    @Test
    public void ParseRequest_HttpVerShouldBeHTTP1dot1()
    {
        RequestParser parser = new RequestParser(stringGETRequest);
        String actual = parser.getHttpVersion();
        assertEquals("HTTP/1.1", actual);
    }
    
    @Test
    public void ParseRequest_PathShouldBeFoldersAndPNG()
    {
        RequestParser parser = new RequestParser(stringGETRequestWithLongerPath);
        String actual = parser.getPath();
        assertEquals("/myFolder/secondFolder/image.png", actual);
    }
    
    @Test
    public void ParseRequest_TypeShouldBePOST()
    {
        RequestParser parser = new RequestParser(stringPostRequest);
        RequestType actual = parser.getType();
        assertEquals(RequestType.POST, actual);
    }
    
    @Test
    public void ParseRequest_TypeShouldBeILLEGAL()
    {
        RequestParser parser = new RequestParser(stringFAULTRequest);
        RequestType actual = parser.getType();
        assertEquals(RequestType.ILLEGAL, actual);
    }

    
}
    

    