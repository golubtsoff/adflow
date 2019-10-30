package exception;

import java.util.logging.Level;
import java.util.logging.Logger;

public class NotFoundException extends Exception {
    private static final Logger logger = Logger.getLogger(NotFoundException.class.getName());

    public NotFoundException(String message, Exception e) {
        super(message, e);
        logger.log(Level.WARNING, e.getMessage(), e);
    }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(Exception e) {
        this(e.getMessage(), e);
    }

    public NotFoundException(){
        super();
    }
}
