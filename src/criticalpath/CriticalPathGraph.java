package criticalpath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

public class CriticalPathGraph {


    private ArrayList<Task> tasks = new ArrayList<>();

    public CriticalPathGraph(){
        Task start = new Task("_START_", (float)0.0, new ArrayList<>());
        Task end = new Task("_END_", (float)0.0 ,  new ArrayList<>());
        this.tasks.add(start);
        end.addPredecessor(start);
        this.tasks.add(end);

    }

    public ArrayList<Task> getTasks() {
        return this.tasks;
    }

    public void addTask(Task task) throws Exception {

        //check if task is duplicate
        for (Task tsk: this.getTasks()){
            if (tsk.getId().equals(task.getId())){
                throw new Exception(" a duplicate task");
            }
        }
        //Check if activity is a start activity
        if (task.getPredecessors().isEmpty() && !task.getId().equals("_START_")) {
            System.out.println(task.getId() + " is a start activity");
            task.getPredecessors().add(this.getTask("_START_"));
        }

        //Check if any of the task's predecessors are a final activity
        for (Task tsk : task.getPredecessors()){
            System.out.println(tsk.getId() + " is a predecessor of " + task.getId());
            if (this.getEndTasks().contains(tsk)){
                System.out.println(task.getId() + " is an end activity");
                this.getTask("_END_").getPredecessors().remove(tsk);
                if(!this.getTask("_END_").getPredecessors().contains(task)){
                    this.getTask("_END_").addPredecessor(task);
                }
            }
        }

        //Check if task has any successor tasks, otherwise make it a predecessor of _END_
        if (getSuccessorTasks(task).isEmpty()){
            if (!this.getTask("_END_").getPredecessors().contains(task)){//check for duplicates
                this.getTask("_END_").addPredecessor(task);
            }

        }

        this.tasks.add(task);
        this.assignStartEndTimes();
    }

    public void deleteTask(Task task) throws Exception {
        if (task.getId().equals("_START_") || task.getId().equals("_END_")) {
            throw new Exception("Attempted deletion of _START_/_END_ node");
        }

        this.tasks.remove(task);
        for (Task task1: this.getSuccessorTasks(task)){
            task1.getPredecessors().remove(task);
            task1.addPredecessors(task.getPredecessors());
        }
    }

    private ArrayList<Task> getStartTasks(){
        return getSuccessorTasks(this.getTask("_START_"));
    }

    private ArrayList<Task> getEndTasks(){
        return this.getTask("_END_").getPredecessors();
    }

    public Task getTask(String id){
        for (Task task : this.tasks){
            if (task.getId().equals(id)){
                return task;
            }
        }
        try {
          throw new Exception("Task not found");
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Task> getSuccessorTasks(Task target){
        ArrayList<Task> tasks = new ArrayList<>();
        for (Task task : this.tasks){
            if (task.getPredecessors().contains(target)){
                tasks.add(task);
            }
        }
        return tasks;
    }

    private Boolean checkIfAllPredecessorsSearched(Task task, HashMap<Task, Boolean> searched){
        for (Task tsk : task.getPredecessors()){
            if (!searched.get(tsk)){
                return false;
            }
        }
        return true;
    }

    private Boolean checkIfAllSuccessorsSearched(Task task, HashMap<Task, Boolean> searched){
        for (Task tsk : getSuccessorTasks(task)){
            if (!searched.get(tsk)){
                return false;
            }
        }
        return true;
    }

    private void assignStartEndTimes(){
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

        while (!searched.get(getTask("_END_"))){
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
        queue.addLast(this.getTask("_END_"));
        searched.replace(this.getTask("_END_"), true);
        this.getTask("_END_").setLatestFinishTime(getTask("_END_").getEarlyStartTime());
        /*
        for (Task task : this.getEndTasks()){
            //System.out.println(task.getId());
            task.setLatestFinishTime(task.getEarlyStartTime()+task.getDuration());
            queue.addLast(task);
            searched.replace(task, true);
        }
        */

        while (!searched.get(getTask("_START_"))){
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

    public ArrayList<ArrayList<Task>> getCriticalPaths(){
        ArrayList<ArrayList<Task>> criticalPaths = new ArrayList<>();
        HashMap<Task, Boolean> searched = new HashMap<>();
        LinkedList<Task> queue = new LinkedList<>();

        for (Task task : this.tasks){
            searched.put(task, false);
            System.out.println(task.getId());
        }

        ArrayList<Task> cp1 = new ArrayList<>();
        cp1.add(this.getTask("_END_"));
        criticalPaths.add(cp1);
        queue.addAll(this.getTask("_END_").getPredecessors());

        while (!searched.get(this.getTask("_START_"))){
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
            cp.remove(this.getTask("_END_"));
            cp.remove(this.getTask("_START_"));
        }

        return criticalPaths;
    }
}