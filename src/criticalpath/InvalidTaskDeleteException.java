package criticalpath;

/**
 * An exception to be used when a start/end task is attempted to be deleted.
 * @author Cameron Sabuda
 */
public class InvalidTaskDeleteException extends Exception {
    public InvalidTaskDeleteException(String message) {
        super(message);
    }
}
