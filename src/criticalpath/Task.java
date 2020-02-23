package criticalpath;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;

public class Task implements Comparable{

    private FloatProperty earlyStartTime;
    private FloatProperty latestFinishTime;
    private StringProperty id;
    private FloatProperty duration;
    private ArrayList<Task> predecessors = new ArrayList<>();


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

    @Override
    public int compareTo(Object t) {
        return -Math.round(((Task) t).getEarlyStartTime());
    }

    public FloatProperty durationProperty() {
        if (duration == null) duration = new SimpleFloatProperty(this, "duration");
        return duration;
    }

    public FloatProperty earlyStartTimeProperty() {
        if (earlyStartTime == null) earlyStartTime = new SimpleFloatProperty(this, "earlyStartTime");
        return earlyStartTime;
    }

    public FloatProperty latestFinishTimeProperty() {
        if (latestFinishTime == null) latestFinishTime = new SimpleFloatProperty(this, "latestFinishTime");
        return latestFinishTime;
    }

    public StringProperty idProperty() {
        if (id == null) id = new SimpleStringProperty(this, "id");
        return id;
    }

    public void addPredecessor(Task task){

        if (!this.predecessors.isEmpty() //This condition needed because otherwise outOfBounds exception
         && this.predecessors.get(0).getId().equals("_START_")){
            this.predecessors.remove(0);
        }
        this.predecessors.add(task);
    }

    public void addPredecessors(ArrayList<Task> predecessors){
        for (Task task: predecessors){
            this.addPredecessor(task);
        }
    }

    public ArrayList<Task> getPredecessors(){ return this.predecessors; }

    public String getId() {
        return this.id.get();
    }

    public Float getEarlyStartTime() {
        return earlyStartTimeProperty().get();
    }

    public void setEarlyStartTime(Float earlyStartTime){ earlyStartTimeProperty().set(earlyStartTime); }

    public Float getLatestFinishTime() {
        return this.latestFinishTime.get();
    }

    public void setLatestFinishTime(Float latestFinishTime){ latestFinishTimeProperty().set(latestFinishTime); }

    public void setId(String id){ idProperty().set(id); }

    public void setDuration(Float duration){ durationProperty().set(duration);}

    public Float getDuration(){ return durationProperty().get();}
}
