package criticalpath;

/**
 * An exception to be thrown a task to be added to a graph is already in the graph (or a task in the graph has the same
 * id)
 * @author Cameron Sabuda
 */
public class DuplicateTaskException extends Exception {
    public DuplicateTaskException(String message) {
        super(message);
    }
}
