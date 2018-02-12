import org.junit.Assert;
import org.junit.Test;


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
    
    @Test
    public void ParseRequest_TypeShouldBeGET()
    {
        RequestParser parser = new RequestParser(stringGETRequest);
        String actual = parser.getType();
        Assert.assertEquals("GET", actual);
    }
    
    @Test
    public void ParseRequest_PathShouldBeSlash()
    {
        RequestParser parser = new RequestParser(stringGETRequest);
        String actual = parser.getPath();
        Assert.assertEquals("/", actual);
    }
    
    @Test
    public void ParseRequest_HttpVerShouldBeHTTP1dot1()
    {
        RequestParser parser = new RequestParser(stringGETRequest);
        String actual = parser.getHttpVersion();
        Assert.assertEquals("HTTP/1.1", actual);
    }
    
    @Test
    public void ParseRequest_PathShouldBeFoldersAndPNG()
    {
        RequestParser parser = new RequestParser(stringGETRequestWithLongerPath);
        String actual = parser.getPath();
        Assert.assertEquals("/myFolder/secondFolder/image.png", actual);
    }
}
    

    