package nl.remideboer.webserver;

/**
 *
 * @author Remi
 */
public class MethodNotAllowedException extends Exception{

    public MethodNotAllowedException(String m) {
        super(m);
    }
    
}
