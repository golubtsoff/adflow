package exception;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Evgeniy Golubtsov on 18.02.2018.
 */
public class DbException extends Exception {

    private static final Logger logger = Logger.getLogger(DbException.class.getName());

    public DbException(String message, Exception e) {
        super(message, e);
        logger.log(Level.WARNING, e.getMessage(), e);
    }

    public DbException(String message) {
        this(message, null);
    }

    public DbException(Exception e) {
        this(e.getMessage(), e);
    }
}
