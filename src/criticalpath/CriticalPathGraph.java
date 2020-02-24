package criticalpath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Represents the critical path network as a whole. Part of the model.
 * @author Cameron Sabuda
 */
public class CriticalPathGraph {

    /**
     * The tasks in the network.
     */
    private ArrayList<Task> tasks = new ArrayList<>();

    /**
     * Creates a critical path graph with a start and end node of weight 0.
     */
    public CriticalPathGraph() {
        Task start = new Task("_START_", (float)0.0, new ArrayList<>());
        Task end = new Task("_END_", (float)0.0 ,  new ArrayList<>());
        this.tasks.add(start);
        end.addPredecessor(start);
        this.tasks.add(end);
    }

    /**
     * Returns the tasks in the network.
     * @return An ArrayList of all tasks in the network.
     */
    public ArrayList<Task> getTasks() {
        return this.tasks;
    }

    /**
     * Adds a task to the network. If the task is a duplicate task, an exception is thrown.
     * @param task The task to be added to the network
     * @throws DuplicateTaskException Thrown if the task to be added shares an id with a task already in the graph.
     */
    public void addTask(Task task) throws DuplicateTaskException {

        //check if task is duplicate
        for (Task tsk: this.getTasks()){
            if (tsk.getId().equals(task.getId())) {
                throw new DuplicateTaskException(" a duplicate task");
            }
        }
        //Check if activity is a start activity
        if (task.getPredecessors().isEmpty() && !task.getId().equals("_START_")) {
            // System.out.println(task.getId() + " is a start activity");
            task.getPredecessors().add(this.getStartTask());
        }

        //Check if any of the task's predecessors are a final activity
        for (Task tsk : task.getPredecessors()) {
            // System.out.println(tsk.getId() + " is a predecessor of " + task.getId());
            if (this.getEndTasks().contains(tsk)) {
                // System.out.println(task.getId() + " is an end activity");
                this.getEndTask().getPredecessors().remove(tsk);
                if(!this.getEndTask().getPredecessors().contains(task)){
                    this.getEndTask().addPredecessor(task);
                }
            }
        }

        //Check if task has any successor tasks, otherwise make it a predecessor of _END_
        if (getSuccessorTasks(task).isEmpty()) {
            if (!this.getEndTask().getPredecessors().contains(task)){//check for duplicates
                this.getEndTask().addPredecessor(task);
            }

        }

        this.tasks.add(task);
        this.assignStartEndTimes();
    }

    /**
     * Deletes a task from the network. If the start/end nodes are attempted to be deleted then an exception is thrown.
     * @param task The task to be deleted from the network.
     * @throws InvalidTaskDeleteException Thrown if the task to be deleted is the start or end task node which have to
     * be kept.
     */
    public void deleteTask(Task task) throws InvalidTaskDeleteException {

        // check if task is the start/end node
        if (task.getId().equals("_START_") || task.getId().equals("_END_")) {
            throw new InvalidTaskDeleteException("Attempted deletion of _START_/_END_ node");
        }

        this.tasks.remove(task);

        // make sure linked tasks aren't unlinked
        for (Task task1: this.getSuccessorTasks(task)){
            task1.getPredecessors().remove(task);
            task1.addPredecessors(task.getPredecessors());
        }
    }

    /**
     * Gets the starting tasks in the network.
     * @return A list of tasks containing the starting tasks.
     */
    private ArrayList<Task> getStartTasks(){
        return getSuccessorTasks(this.getStartTask());
    }

    /**
     * Gets the end tasks of a network.
     * @return A list of tasks containing the end tasks.
     */
    private ArrayList<Task> getEndTasks() {
        try {
            return this.getTask("_END_").getPredecessors();
        }
        catch (Exception e) {
            // This should never happen but is here just in case
            System.err.println("End task not found.");
            return null;
        }
    }

    /**
     * Gets a task by their id string.
     * @param id The id of the required task.
     * @return The task that has the id given.
     * @exception TaskNotFoundException Thrown if task to retrieve doesn't exist.
     */
    public Task getTask(String id) throws TaskNotFoundException {
        for (Task task : this.tasks){
            if (task.getId().equals(id)){
                return task;
            }
        }
        throw new TaskNotFoundException("Task not found");
    }

    /**
     * A specific version of getTask() that gets the start task. This doesn't throw an exception as getting the start
     * task should never cause an exception and saves having to deal with exception from getting tasks if they are just
     * the start task.
     * @return The start task.
     */
    public Task getStartTask() {
        try {
            return this.getTask("_START_");
        }
        catch (TaskNotFoundException e) {
            System.err.println("Start task doesn't exist"); // This really shouldn't ever happen.
            return null;
        }
    }

    /**
     * A specific version of getTask() that gets the end task. This doesn't throw an exception as getting the end
     * task should never cause an exception and saves having to deal with exception from getting tasks if they are just
     * the end task.
     * @return The end task.
     */
    public Task getEndTask() {
        try {
            return this.getTask("_END_");
        }
        catch (TaskNotFoundException e) {
            System.err.println("End task doesn't exist"); // This really shouldn't ever happen.
            return null;
        }
    }

    /**
     * Gets the successors of a task by iterating through all of the tasks in the network and checking its predecessors
     * @param task The task get the successors of.
     * @return A list of tasks that contain the task as a predecessor
     */
    public ArrayList<Task> getSuccessorTasks(Task task){
        ArrayList<Task> tasks = new ArrayList<>();
        for (Task t : this.tasks){
            if (t.getPredecessors().contains(task)){
                tasks.add(t);
            }
        }
        return tasks;
    }

    /**
     * Helper method for AssignStartEndTimes that checks if a given task's predecessors have already been searched.
     * @param task The task whose predecessors we desire to check
     * @param searched A mapping of tasks to a boolean which represents if they have been searched or not.
     * @return True if searched, false if not.
     */
    private Boolean checkIfAllPredecessorsSearched(Task task, HashMap<Task, Boolean> searched){
        for (Task t : task.getPredecessors()){
            if (!searched.get(t)){
                return false;
            }
        }
        return true;
    }

    /**
     * Helper method for AssignStartEndTimes that checks if a given task's successors have already been searched.
     * @param task The task whose successors we desire to check
     * @param searched A mapping of tasks to a boolean which represents if they have been searched or not.
     * @return True if searched, false if not.
     */
    private Boolean checkIfAllSuccessorsSearched(Task task, HashMap<Task, Boolean> searched){
        for (Task tsk : getSuccessorTasks(task)){
            if (!searched.get(tsk)){
                return false;
            }
        }
        return true;
    }

    /**
     * A method that calculates the earliest start and latest finish times of all the tasks in the network and assigns
     * them to the tasks. Used when a task is added because it can affect the start/finish times of other tasks.
     */
    private void assignStartEndTimes() {
        if (this.tasks.size() == 2){
            return;
        }
        LinkedList<Task> queue = new LinkedList<>();
        HashMap<Task, Boolean> searched = new HashMap<>();

        //SET STARTS TIMES
        //add all tasks to searched check dict
        //System.out.println("Setting start times");
        //System.out.println("Listing tasks");
        for (Task task : this.tasks){
            searched.put(task, false);
            System.out.println(task.getId());
        }

        System.out.println("listing start tasks");
        //set start tasks as start 0 and marks them as searched
        for (Task task : this.getStartTasks()){
            //System.out.println( task.getId());
            task.setEarlyStartTime((float)0);
            queue.addLast(task);
            searched.replace(task, true);
        }

        while (!searched.get(getEndTask())){
            Task currentTask = queue.pop();

            //Check if successor tasks from visited activity have had their predecessors' start times defined yet
            for (Task task: getSuccessorTasks(currentTask)){
                //Check if the task has had all of its predecessors searched yet
                if (checkIfAllPredecessorsSearched(task, searched)){
                    Float earlyStartTime = (float)0;
                    //Find latest start time
                    for (Task task1: task.getPredecessors()){
                        //earlyStartTime = (earlyStartTime < (task1.getDuration() + task1.getEarlyStartTime())) ? (task1.getDuration() + task1.getEarlyStartTime()) : earlyStartTime;
                        if (earlyStartTime < (task1.getDuration() + task1.getEarlyStartTime())){
                            earlyStartTime = task1.getDuration() + task1.getEarlyStartTime();
                        }
                    }
                    task.setEarlyStartTime(earlyStartTime);
                    searched.replace(task, true);

                    //Add task to queue to be processed next
                    queue.addLast(task);
                }
            }
        }

        //SET END TIMES (works the same way but backwards)
        //System.out.println("Setting end times");
        //reset queue
        queue.clear();
        //System.out.println("listing tasks");
        //add all tasks to searched check dict
        for (Task task : this.tasks){
            //System.out.println(task.getId());
            searched.replace(task, false);
        }

        System.out.println("listing end tasks");
        //set end tasks earliest end time equal to start time and marks them as searched
        queue.addLast(this.getEndTask());
        searched.replace(this.getEndTask(), true);
        this.getEndTask().setLatestFinishTime(getEndTask().getEarlyStartTime());
        /*
        for (Task task : this.getEndTasks()){
            //System.out.println(task.getId());
            task.setLatestFinishTime(task.getEarlyStartTime()+task.getDuration());
            queue.addLast(task);
            searched.replace(task, true);
        }
        */

        while (!searched.get(getStartTask())){
            Task currentTask = queue.pop();

            //Check if successor tasks from visited activity have had their successors' late start times defined yet
            for (Task task: currentTask.getPredecessors()){
                //Check if the task has had all of its predecessors searched yet
                if (checkIfAllSuccessorsSearched(task, searched)){
                    Float latestFinishTime = Float.MAX_VALUE;            //Find latest finish time
                    for (Task task1: getSuccessorTasks(task)){
                        //System.out.println(task1.getId() + task1.getLatestFinishTime() + "-" + task1.getDuration());
                        if (latestFinishTime > (task1.getLatestFinishTime() - task1.getDuration())){
                            latestFinishTime = task1.getLatestFinishTime() - task1.getDuration();

                        }
                    }
                    task.setLatestFinishTime(latestFinishTime);
                    //System.out.println(task.getId() + " LFT set to " + latestFinishTime );
                    searched.replace(task, true);

                    //Add task to queue to be processed next
                    queue.addLast(task);
                }
            }
        }


    }

    /**
     * Gets all the critical paths that are in the network.
     * @return A list of list of tasks that each represent a critical path.
     */
    public ArrayList<ArrayList<Task>> getCriticalPaths(){
        ArrayList<ArrayList<Task>> criticalPaths = new ArrayList<>();
        HashMap<Task, Boolean> searched = new HashMap<>();
        LinkedList<Task> queue = new LinkedList<>();

        for (Task task : this.tasks){
            searched.put(task, false);
            System.out.println(task.getId());
        }

        ArrayList<Task> cp1 = new ArrayList<>();
        cp1.add(this.getEndTask());
        criticalPaths.add(cp1);
        queue.addAll(this.getEndTask().getPredecessors());

        while (!searched.get(this.getStartTask())){
            Task task = queue.pop();
            System.out.println("Searching " + task.getId());
            if (task.getLatestFinishTime() - task.getDuration() == task.getEarlyStartTime()){
                System.out.println(task.getId() + " is critical");
                boolean inExistingCriticalPath = false;
                //check if task's successor is last in critical path before adding it
                for (ArrayList<Task> cp : criticalPaths){
                    if (cp.get(cp.size()-1).getPredecessors().contains(task)){
                        cp.add(task);
                        inExistingCriticalPath = true;
                    }
                }
                if (!inExistingCriticalPath){
                    ArrayList<Task> cp = new ArrayList<>();
                    cp.addAll(criticalPaths.get(0));
                    cp.remove(cp.size()-1);
                    cp.add(task);
                    criticalPaths.add(cp);
                }
                for (Task tsk: task.getPredecessors()){
                    if (!searched.get(tsk)){
                        queue.addLast(tsk);
                    }
                }
                searched.replace(task, true);
            }
        }

        for(ArrayList<Task> criticalPath : criticalPaths){
            Collections.sort(criticalPath);
        }

        //remove _START_ and _END_ as they will always be critical and the user doesn't need to see them
        for (ArrayList<Task> cp : criticalPaths){
            cp.remove(this.getEndTask());
            cp.remove(this.getStartTask());
        }

        return criticalPaths;
    }
}