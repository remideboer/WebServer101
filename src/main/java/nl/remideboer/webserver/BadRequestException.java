package nl.remideboer.webserver;

/**
 *
 * @author Remi
 */
public class BadRequestException extends Exception{

    public BadRequestException(String message) {
        super(message);
    }
    
}
