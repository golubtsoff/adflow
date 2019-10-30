package exception;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ConflictException extends Exception {
    private static final Logger logger = Logger.getLogger(ConflictException.class.getName());

    public ConflictException(String message, Exception e) {
        super(message, e);
        logger.log(Level.WARNING, e.getMessage(), e);
    }

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(Exception e) {
        this(e.getMessage(), e);
    }

    public ConflictException(){
        super();
    }
}
