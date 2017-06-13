package nl.remideboer.webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TODO Builder patroon
 *
 * @author Remi
 */
public class Request {

    private String protocol;
    private String method;
    private String resource;
    private String postData;
    private RequestMethod requestMethod;
    private Map<String, String> headers;
    private Map<String, String> cookies;
    private List<Header> headerList;
    private byte[] body;

    public Request(InputStream instream) throws MethodNotAllowedException{

        try {
            // read first line
            InputStreamReader inreader = new InputStreamReader(instream);
            BufferedReader bufreader = new BufferedReader(inreader);
            String[] requestLine = bufreader.readLine().split(" ");
            // check for proper format of request or else throw BadRequestException
            
            if (requestLine[0].equalsIgnoreCase("GET")) {
                this.requestMethod = RequestMethod.GET;
            } else if (requestLine[0].equalsIgnoreCase("POST")) {
                this.requestMethod = RequestMethod.POST;
            } else {
                // bad request
                throw new MethodNotAllowedException("Receive method [" + requestLine[0] + "] not supported or unknown");
            }

            this.resource = requestLine[1];
            this.protocol = requestLine[2];

            this.headers = new HashMap<>();
            this.cookies = new HashMap<>();
            this.headerList = new ArrayList<>();

            processHeaders(bufreader);
            processPostData(bufreader);

        } catch (IOException ex) {
            Logger.getLogger(Request.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getMethod() {
        return method;
    }

    public String getResource() {
        return resource;
    }

    public Map<String, String> getHeaderMap() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getPostData() {
        return postData;
    }
    
    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    // if we encounter the cookie header split further, accept types, language etc in future as well
    public void addHeader(String key, String value) {
        if (key.equals("Cookie")) {
            processCookieHeader(value);
        } // store cookies in header map as well
        headers.put(key, value);
        headerList.add(new Header(key, value)); // zinvol?
    }

    private void processCookieHeader(String value) {
        // value heeft format: testCookie=testCookieValue; testCookie2=Andere2017-04-26T13
        // fisrt split on ';'
        String[] cookiePairs = value.split(";");
        for (String pair : cookiePairs) {
            // split again on '=' and trim
            String[] split = pair.split("=");
            this.addCookie(split[0].trim(), split[1].trim());
        }
    }

    // add header using array length two
    public void addHeader(String[] arr) {
        if (arr.length != 2) {
            throw new IllegalArgumentException("Array is not the ritgh size to contain header and value");
        }
        if (arr[0].equals("Cookie")) {
            processCookieHeader(arr[1]);
        }
        headers.put(arr[0].trim(), arr[1].trim());
    }

    public void addCookie(String key, String value) {
        cookies.put(key, value);
    }

    public Map<String, String> getCookieMap() {
        return this.cookies;
    }
    // vervang met apache commons ToStringBuilder

    @Override
    public String toString() {
        return "Request{" + "protocol=" + protocol + ", resource=" + resource + ", postData=" + postData + ", requestMethod=" + requestMethod + ", headers=" + headers + ", cookies=" + cookies + ", headerList=" + headerList + '}';
    }

    private void processHeaders(BufferedReader bufreader) throws IOException {
        String inStr;
        // get headers
        while ((inStr = bufreader.readLine()) != null) {
            // if we encounter newline newline header part has ended
            if (inStr.equals("")) {
                break;
            }
            // string split, param int says to only return array of 2 if split can be done, n-1 times pattern will be applied    
            this.addHeader(inStr.split(":", 2)); // convenience method split only on first occurence http://stackoverflow.com/questions/18462826/split-string-only-on-first-instance-java
        }
    }

    private void processPostData(BufferedReader bufreader) {
        if (this.getRequestMethod() == RequestMethod.POST) {
            // read post
            if (this.getHeaderMap().containsKey("Content-Length")) {
                int length = Integer.parseInt(this.getHeaderMap().get("Content-Length"));
                if (length > 0) {
                    // read length bytes
                    char[] buffer = new char[length];
                    System.out.println("Reading " + length + "bytes");
                    try {
                        bufreader.read(buffer, 0, length); // read n-chars/bytes from bufreader to char buffer
                    } catch (IOException ex) {
                        Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    postData = new String(buffer, 0, buffer.length);
                }
            }

        }
    }

}
