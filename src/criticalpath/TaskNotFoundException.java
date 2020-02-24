package criticalpath;

/**
 * An exception to be thrown if a task is not found to be in a critical path network
 * @author Cameron Sabuda
 */
public class TaskNotFoundException extends Exception {
    public TaskNotFoundException(String message) {
        super(message);
    }
}
