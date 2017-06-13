package nl.remideboer.webserver;

import java.util.HashMap;
import java.util.Map;

/**
 *  TODO Builder patroon
 * @author Remi
 */
public class Response {
    private String protocol;
    private String method;
    private String resource;
    private Map<String, String> headers;
    private Map<String, String> cookies;
    private byte[] body;

    public Response(String method, String resource, String protocol) {
        this.protocol = protocol;
        this.method = method;
        this.resource = resource;
        this.headers = new HashMap<>();
        this.cookies = new HashMap<>();
    }
    
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public Map<String, String> getHeaderMap() {
        return headers;
    }

    public void setHeaderMap(Map<String, String> headers) {
        this.headers = headers;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public String getProtocol() {
        return protocol;
    }

    // if we encounter the cookie header split further, accept types, language etc in future as well
    public void addHeader(String key, String value){
        if(key.equals("Cookie")){
            processCookieHeader(value);
        } // store cookies in header map as well
        headers.put(key, value);
    }

    private void processCookieHeader(String value) {
        // value heeft format: testCookie=testCookieValue; testCookie2=Andere2017-04-26T13
        // fisrt split on ';'
        String[] cookiePairs = value.split(";");
        for(String pair : cookiePairs){
            // split again on '=' and trim
            String[] split = pair.split("=");
            this.addCookie(split[0].trim(), split[1].trim());
        }
    }
    
    // add header using array length two
    public void addHeader(String[] arr){
        if(arr.length != 2 ) throw new IllegalArgumentException("Array is not the ritgh size to contain header and value");
        if(arr[0].equals("Cookie")){
            processCookieHeader(arr[1]);
        }
        headers.put(arr[0].trim(), arr[1].trim());
    }
    
    public void addCookie(String key, String value){
        cookies.put(key, value);
    }
    
    public Map<String, String> getCookieMap(){
        return this.cookies;
    }
    // vervang met apache commons ToStringBuilder
    @Override
    public String toString() {
        return "Request{" + "protocol=" + protocol + ", method=" + method + ", resource=" + resource + ", headers=" + headers + ", cookies=" + cookies + ", body=" + body + '}';
    }
   
    
    
}
