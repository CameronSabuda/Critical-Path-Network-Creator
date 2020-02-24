package criticalpath;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;

/**
 * Represents a task/activity to be stored in a critical path network.
 * @author Cameron Sabuda
 */
public class Task implements Comparable{

    private FloatProperty earlyStartTime;
    private FloatProperty latestFinishTime;
    private StringProperty id;
    private FloatProperty duration;
    private ArrayList<Task> predecessors = new ArrayList<>();

    /**
     * Creates a task with a given id, duration and predecessors.
     * @param id The id/name of a task in the form of a string.
     * @param duration The duration of the task.
     * @param predecessors A list of all the tasks that will be the predecessors of this task.
     */
    public Task(String id, Float duration, ArrayList<Task> predecessors) {
        if (duration < 0){
            throw new RuntimeException(" an invalid duration on task");
        }

        this.setId(id);
        this.setDuration(duration);
        this.addPredecessors(predecessors);
        this.setEarlyStartTime((float)0);
        this.setLatestFinishTime((float)0);
    }

    /**
     * Compares tasks based on their early start times.
     * @param t Task to be compared to.
     * @return 0 - the other task's early start time.
     */
    @Override
    public int compareTo(Object t) {
        return -Math.round(((Task) t).getEarlyStartTime());
    }

    /**
     * Get the durationProperty variable used to store the duration.
     * @return The durationProperty used to store the duration.
     */
    public FloatProperty durationProperty() {
        if (duration == null) duration = new SimpleFloatProperty(this, "duration");
        return duration;
    }

    /**
     * Get the earlyStartTimeProperty variable used to store the early start time.
     * @return The EarlyStartTimeProperty used to store the early start time.
     */
    public FloatProperty earlyStartTimeProperty() {
        if (earlyStartTime == null) earlyStartTime = new SimpleFloatProperty(this, "earlyStartTime");
        return earlyStartTime;
    }

    /**
     * Get the latestStartTimeProperty variable used to store the latest start time.
     * @return The latestStartTimeProperty used to store the latest start time.
     */
    public FloatProperty latestFinishTimeProperty() {
        if (latestFinishTime == null) latestFinishTime = new SimpleFloatProperty(this, "latestFinishTime");
        return latestFinishTime;
    }

    /**
     * Get the idProperty variable used to store the id.
     * @return The idProperty used to store the id.
     */
    public StringProperty idProperty() {
        if (id == null) id = new SimpleStringProperty(this, "id");
        return id;
    }

    /**
     * Adds a predecessor to the task.
     * @param task The new task of which you want this task to be a predecessor of.
     */
    public void addPredecessor(Task task){

        if (!this.predecessors.isEmpty() //This condition needed because otherwise outOfBounds exception
         && this.predecessors.get(0).getId().equals("_START_")){
            this.predecessors.remove(0);
        }
        this.predecessors.add(task);
    }

    /**
     * Adds a predecessor to the task.
     * @param predecessors The a list of the tasks of which you want this task to be a predecessor of.
     */
    public void addPredecessors(ArrayList<Task> predecessors){
        for (Task task: predecessors){
            this.addPredecessor(task);
        }
    }

    /**
     * Gets the predecessors of this task.
     * @return A list of all the tasks that are a predecessor of this task.
     */
    public ArrayList<Task> getPredecessors(){ return this.predecessors; }

    /**
     * Gets the task's id.
     * @return The task's id.
     */
    public String getId() {
        return this.id.get();
    }

    /**
     * Gets the early start time of the task.
     * @return The early start time of the task.
     */
    public Float getEarlyStartTime() {
        return earlyStartTimeProperty().get();
    }

    /**
     * Sets the early start time of the task.
     * @param earlyStartTime The value to set the early start time to.
     */
    public void setEarlyStartTime(Float earlyStartTime){ earlyStartTimeProperty().set(earlyStartTime); }

    /**
     * Gets the latest start time of the task.
     * @return The latest start time of the task.
     */
    public Float getLatestFinishTime() {
        return this.latestFinishTime.get();
    }

    /**
     * Sets the latest start time of the task.
     * @param latestFinishTime The value to set the latest finish time to.
     */
    public void setLatestFinishTime(Float latestFinishTime){ latestFinishTimeProperty().set(latestFinishTime); }

    /**
     * Sets the task's id.
     * @param id The new id of the task.
     */
    public void setId(String id){ idProperty().set(id); }

    /**
     * Sets the duration of the task.
     * @param duration The new duration of the task.
     */
    public void setDuration(Float duration){ durationProperty().set(duration);}

    /**
     * Gets the duration of the task.
     * @return The duration of the task.
     */
    public Float getDuration(){ return durationProperty().get();}


}
