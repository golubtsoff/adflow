package exception;

import java.util.logging.Level;
import java.util.logging.Logger;

public class BadRequestException extends Exception {

    private static final Logger logger = Logger.getLogger(BadRequestException.class.getName());

    public BadRequestException(String message, Exception e) {
        super(message, e);
        logger.log(Level.WARNING, e.getMessage(), e);
    }

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(Exception e) {
        this(e.getMessage(), e);
    }

    public BadRequestException(){
        super();
    }
}
