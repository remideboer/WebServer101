package nl.remideboer.webserver;

/**
 *
 * @author Remi
 */
public class Header {
    private final String key;
    private final String value;
    
    public Header(String key, String value){
        this.key = key;
        this.value = value;
    }
    
    public String getKey(){
        return this.key;
    }
    
    public String getValue(){
        return this.value;
    }
}
